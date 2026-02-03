package com.example.voicesurgerywhisper

import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.voicesurgerywhisper.data.AuthRepository
import com.example.voicesurgerywhisper.data.PdfUploadRepository
import com.example.voicesurgerywhisper.network.enviarParaWhisper
import com.example.voicesurgerywhisper.services.AudioRecorder
import com.example.voicesurgerywhisper.services.VoiceCommandProcessor
import com.example.voicesurgerywhisper.ui.AnswerExtractor
import com.example.voicesurgerywhisper.ui.PdfFlowManager
import com.example.voicesurgerywhisper.ui.QuestionViewFactory
import com.example.voicesurgerywhisper.utils.CsvDataSaver
import com.example.voicesurgerywhisper.utils.PermissionManager
import com.example.voicesurgerywhisper.viewmodel.QuizStateManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recorder: AudioRecorder
    private var currentAudioPath: String = ""


    //private lateinit var textView: TextView
    //private lateinit var btnFalar: Button
    //private lateinit var btnSalvacsv: Button
    private lateinit var etA : EditText
    private lateinit var etB : EditText

    private val authRepository = AuthRepository() // Crie uma instância do repositório

    lateinit var jwtToken: String

    //Adicionado para equiparar com o projeto maior

    annotation class LoginResponse

    private var campoSelecionado: EditText? = null

    private val stateManager = QuizStateManager()

    private lateinit var viewFactory: QuestionViewFactory

    private val answerExtractor = AnswerExtractor()

    private lateinit var dataSaver: CsvDataSaver

    //VoiceRecognizer tá equivalendo aqui pelo AudioRecorder

    private val commandProcessor = VoiceCommandProcessor()

     // Views da UI
    private lateinit var container: FrameLayout

    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button

    private lateinit var btnFalar: Button

    private lateinit var btnHowto: Button
    private lateinit var textView: TextView

    /*//Botões da interface principal
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button

    private lateinit var btnFalar: Button

    private lateinit var btnHowto: Button
    private lateinit var textView: TextView
     */
    private lateinit var currentQuestionView: View

    private lateinit var pdfManager: PdfFlowManager


    override fun onCreate(savedInstanceState: Bundle?) {
        recorder = AudioRecorder(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Adicionais para tentar funcionar o ajuste de layout em tablets [
        val root = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(0, 0, 0, imeInsets.bottom)
            insets
        }
        //  ]


        // Inicializa classes que dependem do Context
        viewFactory = QuestionViewFactory(this)
        dataSaver = CsvDataSaver(this)
        //voiceRecognizer = VoiceRecognizer(this)
        // Passamos 'this' (activity), o repositório e uma função lambda para atualizar o texto
        val uploadRepository = PdfUploadRepository(this)

        pdfManager = PdfFlowManager(
            activity = this,
            uploadRepository = uploadRepository,
            onStatusUpdate = { mensagem ->
                // Esta função roda sempre que o Manager quiser falar algo
                textView.text = mensagem
            }
        )

        // Encontra Views
        textView = findViewById(R.id.textView)
        container = findViewById(R.id.question_container)
        btnNext = findViewById(R.id.btn_next)
        btnPrevious = findViewById(R.id.btn_previous)
        btnFalar = findViewById(R.id.btnFalar)
        btnHowto = findViewById(R.id.btnHowto)

        // Configura Listeners
        btnNext.setOnClickListener { onNextClicked() }
        btnPrevious.setOnClickListener { onPreviousClicked() }
        btnFalar.setOnClickListener { iniciarReconhecimentoVoz() }
        btnHowto.setOnClickListener { mostraInstrucoes() }
        /*
        textView = findViewById(R.id.textView)
        btnFalar = findViewById(R.id.btnFalar)
        btnSalvacsv = findViewById(R.id.btnSalvacsv)
        etA = findViewById(R.id.et_a)
        etB = findViewById(R.id.et_b)


        //solicitarPermissoes()


        btnFalar.setOnClickListener {
            iniciarReconhecimentoVoz()

        }

        btnSalvacsv.setOnClickListener {
            salvarDadosCSV()
        }


        etA.setOnClickListener {
            Toast.makeText(this, "Mostra o nome", Toast.LENGTH_SHORT).show()
            iniciarReconhecimentoVoz()
        }

        etB.setOnClickListener {
            Toast.makeText(this, "Mostra a idade", Toast.LENGTH_SHORT).show()
            iniciarReconhecimentoVoz()
        }
        */

        fazerLogin {
            // Este código aqui (onSuccess) só roda
            // depois que o login for bem-sucedido
            textView.text = "Login feito com sucesso!"
            //Log.d("Login", "Login OK, token pronto para uso.")
            // Você pode, por exemplo, habilitar botões aqui
        }

        PermissionManager.checkAndRequestAudioPermission(this)
        showCurrentQuestion()
        mostraInstrucoes()

    }

    /*
    private fun solicitarPermissoes() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }
     */

    private fun salvarDadosCSV() {
        val nome = etA.text.toString()
        val idade = etB.text.toString()

        //
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        var fileNumber = 1
        var file: File

        do {
            val fileName = "dados$fileNumber.csv"
            file = File(directory, fileName)
            fileNumber++
        } while (file.exists()) // Continua até encontrar um nome de arquivo que não existe

        try {
            val writer = FileWriter(file)
            writer.append("Nome,Idade\n")
            writer.append("$nome,$idade\n")
            writer.flush()
            writer.close()
            textView.text = "Dado salvos em ${file.name}"
        } catch (e: IOException) {
            textView.text = "Erro ao salvar arquivo"
        }

    }



    private fun iniciarReconhecimentoVoz() {

        textView.text = "Gravando..."
        currentAudioPath = recorder.start()

        btnFalar.isEnabled = false

        // grava por 5 segundos, por exemplo
        Handler(Looper.getMainLooper()).postDelayed({
            val path = recorder.stop()
            textView.text = "Enviando para transcrição..."

            lifecycleScope.launch {
                //var texto = enviarParaWhisper(path, jwtToken)
                var texto: String = enviarParaWhisper(path, jwtToken).toString()
                //texto=texto?.uppercase()?.replace(".","")?.replace(",","")
                //texto=texto.uppercase().replace(".","").replace(",","")
                texto=texto.uppercase()
                //textView.text = texto ?: "Erro na transcrição"
                textView.text = "VOCÊ DISSE: $texto"

                // 1. PRIORIDADE: Se o usuário clicou num campo, preenche direto
                if (campoSelecionado != null) {
                    campoSelecionado?.setText(texto)
                    campoSelecionado = null // Limpa para a próxima interação
                    //return // Encerra aqui, não processa comandos
                }
                else {
                    // 2. DEFAULT: Botão falar, usa a lógica de comandos de voz
                    val question = stateManager.getCurrentQuestion()
                    val shouldGoNext = commandProcessor.processCommand(texto, question, currentQuestionView)

                    if (shouldGoNext) {
                        //btnNext.performClick()
                        onNextClicked() //Evita dar o balão
                    }

                }

                /*
                // lógica básica para preencher
                if (texto != null) {
                    if (texto.contains("NOME", true)) {
                        etA.setText(texto.substringAfter("NOME "))
                    }
                    if (texto.contains("IDADE", true)) {
                        etB.setText(texto.substringAfter("IDADE "))
                    }
                }
                 */

                btnFalar.isEnabled = true
            }

        }, 6000)

    }

    private fun onNextClicked() {
        saveCurrentAnswer()

        if (stateManager.isQuizFinished()) {
            showQuizFinishedDialog()
        } else {
            stateManager.moveToNextQuestion()
            showCurrentQuestion()
        }
    }
    private fun onPreviousClicked() {

        saveCurrentAnswer()
        stateManager.moveToPreviousQuestion()
        showCurrentQuestion()


    }

    private fun showCurrentQuestion() {
        val question = stateManager.getCurrentQuestion()

        // Pegamos a resposta da questão principal e a função para buscar das subperguntas
        val savedValue = stateManager.getSavedAnswer(question)

        currentQuestionView = viewFactory.createView(
            question,
            container,
            savedValue,
            { subQ -> stateManager.getSavedAnswer(subQ) } // Provedor de respostas para filhos
        )

        container.removeAllViews()
        container.addView(currentQuestionView)
        configurarCliquesNosInputs(currentQuestionView)
    }

    private fun saveCurrentAnswer() {
        val question = stateManager.getCurrentQuestion()
        val answer = answerExtractor.extractAnswer(question, currentQuestionView)
        stateManager.saveAnswer(answer)
    }



    private fun showQuizFinishedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Fim das perguntas")
            .setMessage("Você completou todas as perguntas!\nSalve o formulário para anexar o PDF.")
            .setPositiveButton("Salvar e Anexar PDF") { _, _ ->

                dataSaver.saveAnswers(stateManager.getFormattedAnswers()) { savedFile ->
                    if (savedFile != null) {

                        // 3. Configure o Manager com os dados atuais
                        pdfManager.currentCsvFile = savedFile
                        pdfManager.currentJwtToken = jwtToken

                        // 4. Inicie a seleção (Isso substitui abrirSeletorDePdf)
                        pdfManager.startPdfSelection()

                    } else {
                        textView.text = "Falha ao salvar CSV. Processo cancelado."
                    }
                }

                //dataSaver.saveAnswers(stateManager.getFormattedAnswers())

                stateManager.reset()
                showCurrentQuestion()
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    private fun configurarCliquesNosInputs(view: View) {
        // Se a view for um EditText, configura o clique
        if (view is EditText) {
            view.setOnClickListener {
                campoSelecionado = view // Marca este campo como o alvo da voz
                iniciarReconhecimentoVoz()
                //Toast.makeText(this, "Ouvindo para: ${view.hint ?: "este campo"}", Toast.LENGTH_SHORT).show()
            }
        }
        // Se for um container (LinearLayout/Group), procura dentro dele (Recursivo)
        else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                configurarCliquesNosInputs(view.getChildAt(i))
            }
        }
    }

    private fun fazerLogin(onSuccess: () -> Unit) {
        // Mostra o status para o usuário ANTES de começar
        textView.text = "Autenticando..."

        authRepository.login(object : AuthRepository.AuthCallback {

            override fun onSuccess(token: String) {
                // SUCESSO!
                jwtToken = token // Salva o token na Activity
                textView.text = "Login automático realizado" // Atualiza a UI
                onSuccess() // Chama o callback original (ex: habilitar botões)
            }

            override fun onError(message: String) {
                // ERRO!
                textView.text = message // Mostra o erro na UI
            }
        })
    }
    private fun mostraInstrucoes() {
        val alert = AlertDialog.Builder(this)
            .setTitle("Como Utilizar o aplicativo")
            .setMessage("Aperte o botão 'Falar' e pronuncie a informação referente ao campo mostrado na parte superior da tela.\n" +
                    "Caso necessário, utilize o teclado virtual para eventuais correções ou marcações para campos objetivos")
            .setPositiveButton("Fechar") { _, _ ->

            }
            //.setNegativeButton("Não enviar agora", null)
            .create()

        alert.show()
    }
}