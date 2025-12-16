package ufcg.example.voicesurgery.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import ufcg.example.voicesurgery.R
import ufcg.example.voicesurgery.data.*
import kotlin.collections.get
import kotlin.compareTo

class QuestionViewFactory(private val context: Context) {

    fun createView(question: Question, container: ViewGroup): View {
        val inflater = LayoutInflater.from(context)

        val layoutId = when (question) {
            is TextInputQuestion -> R.layout.question_text_input
            is MultipleChoiceQuestion -> R.layout.question_multiple_choice
            is CheckboxQuestion -> R.layout.question_checkbox
            is CheckboxQuestion2 -> R.layout.question_checkbox2
            is MultipleChoiceQuestion2 -> R.layout.question_multiple_choice2
            is SalvaTempo -> R.layout.pontos_pausa
        }

        val view = inflater.inflate(layoutId, container, false)
        val title = view.findViewById<TextView>(R.id.question_title)
        title.text = question.title

        when (question) {
            is MultipleChoiceQuestion -> setupMultipleChoice(view, question)
            is CheckboxQuestion -> setupCheckbox(view, question)
            is TextInputQuestion -> { /* Nenhuma configuração extra necessária */ }
            is CheckboxQuestion2 -> setupCheckbox2(view, question)
            is MultipleChoiceQuestion2 -> setupMultipleChoice2(view, question)
            is SalvaTempo -> { /* Nenhuma configuração extra necessária???? */ } //TODO()
        }

        return view
    }

    private fun setupMultipleChoice(view: View, question: MultipleChoiceQuestion) {
        val group = view.findViewById<RadioGroup>(R.id.options_group)
        group.removeAllViews()
        for (option in question.options) {
            val rb = RadioButton(context)
            rb.text = option
            group.addView(rb)
        }
    }

    private fun setupCheckbox(view: View, question: CheckboxQuestion) {
        val container = view.findViewById<LinearLayout>(R.id.checkbox_container)
        container.removeAllViews()
        for (option in question.options) {
            val cb = CheckBox(context)
            cb.text = option
            container.addView(cb)
        }
    }

    private fun setupCheckbox2(view: View, question: CheckboxQuestion2) {
        val container = view.findViewById<LinearLayout>(R.id.checkbox_container)
        container.removeAllViews()
        for (option in question.options) {
            val cb = CheckBox(context)
            cb.text = option
            container.addView(cb)
        }

        val subQuestionsList = question.subQuestions
        //Log.d("ADebugTag","${subQuestionsList.size}")

        //No teste eu coloquei 3 perguntas de texto, e fiz a lógica pra reduzir pra duas - aletrando
        /*
        val title1 = view.findViewById<TextView>(R.id.question_title1)
        title1.text = subQuestionsList[0].title
        val title2 = view.findViewById<TextView>(R.id.question_title2)
        title2.text = subQuestionsList[1].title
        val title3 = view.findViewById<TextView>(R.id.question_title3)
        if(subQuestionsList.size > 2)
            title3.text = subQuestionsList[2].title
        else {
            title3.visibility = View.GONE
            val input3 = view.findViewById<EditText>(R.id.input_answer3)
            input3.visibility = View.GONE
        }
        */
        val title1 = view.findViewById<TextView>(R.id.question_title1)
        title1.text = subQuestionsList[0].title
        val title2 = view.findViewById<TextView>(R.id.question_title2)
        if(subQuestionsList.size > 1)
            title2.text = subQuestionsList[1].title
        else {
            title2.visibility = View.GONE
            val input2 = view.findViewById<EditText>(R.id.input_answer2)
            input2.visibility = View.GONE
        }
    }

    private fun setupMultipleChoice2(view: View, question: MultipleChoiceQuestion2) {
        val group = view.findViewById<RadioGroup>(R.id.options_group)
        group.removeAllViews()
        for (option in question.options) {
            val rb = RadioButton(context)
            rb.text = option
            group.addView(rb)
        }

        val subQuestionsList = question.subQuestions
        //Log.d("ADebugTag","${subQuestionsList.size}")

        //No teste eu coloquei 3 perguntas de texto, e fiz a lógica pra reduzir pra duas - aletrando
        /*
        val title1 = view.findViewById<TextView>(R.id.question_title1)
        title1.text = subQuestionsList[0].title
        val title2 = view.findViewById<TextView>(R.id.question_title2)
        title2.text = subQuestionsList[1].title
        val title3 = view.findViewById<TextView>(R.id.question_title3)
        if(subQuestionsList.size > 2)
            title3.text = subQuestionsList[2].title
        else {
            title3.visibility = View.GONE
            val input3 = view.findViewById<EditText>(R.id.input_answer3)
            input3.visibility = View.GONE
        }
        */
        val title1 = view.findViewById<TextView>(R.id.question_title1)
        title1.text = subQuestionsList[0].title
        val title2 = view.findViewById<TextView>(R.id.question_title2)
        if(subQuestionsList.size > 1)
            title2.text = subQuestionsList[1].title
        else {
            title2.visibility = View.GONE
            val input2 = view.findViewById<EditText>(R.id.input_answer2)
            input2.visibility = View.GONE
        }
    }
}