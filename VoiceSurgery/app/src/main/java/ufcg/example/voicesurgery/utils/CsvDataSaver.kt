package ufcg.example.voicesurgery.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

class CsvDataSaver(private val context: Context) {

    fun saveAnswers(answers: Map<String, String>, onComplete: (File?) -> Unit) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val csvDir = File(directory, "Arquivos CSV")
        if (!csvDir.exists()) {
            csvDir.mkdirs()
        }

        //var fileNumber = 1
        var file: File
        var sujeito = answers["Nome:"]?.replace(" ", "_") ?: "respostas"
        do {
            val fileName = "respostas$sujeito.csv"
            file = File(csvDir, fileName)
            //fileNumber++
        } while (file.exists())

        try {
            val fileOutputStream = FileOutputStream(file)
            val writer = PrintWriter(OutputStreamWriter(fileOutputStream, "UTF-8"))

            writer.println("Pergunta,Resposta")
            // Processa as perguntas e respostas
            answers.forEach { (pergunta, resposta) ->
                // Substitui perguntas específicas por nomes curtos
                val novaPergunta = when (pergunta) {
                    "Antes da Indução Anestésica" -> "parte 1"
                    "Antes da Incisão Cirúrgica" -> "parte 2"
                    "Antes da Saída do Paciente da Sala de Cirurgia" -> "parte 3"
                    else -> pergunta
                }

                // Escreve a pergunta e resposta no arquivo
                writer.println("\"$novaPergunta\",\"$resposta\"")
            }

            /*
            answers.forEach { (pergunta, resposta) ->
                if(pergunta == "Antes da Indução Anestésica"){
                    pergunta = "parte 1"
                }
                else if(pergunta == "Antes da Incisão Cirúrgica"){
                    pergunta = "parte 2"
                }
                else if(pergunta == "Antes da Saída do Paciente da Sala de Cirurgia"){
                    pergunta = "parte 3"
                }
                val resposta = answers[pergunta] ?: ""
                writer.println("\"$pergunta\",\"$resposta\"")
                //writer.println("\"$pergunta\",\"$resposta\"")
            }
            */

            writer.flush()
            writer.close()
            Toast.makeText(context, "Salvo em: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            onComplete(file) // SUCESSO: Retorna o arquivo salvo

        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            onComplete(null) // FALHA: Retorna nulo
        }
    }
}