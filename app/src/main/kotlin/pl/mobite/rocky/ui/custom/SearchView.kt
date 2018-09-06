package pl.mobite.rocky.ui.custom

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.custom_search_view.view.*
import pl.mobite.rocky.R
import pl.mobite.rocky.utils.CustomTextWatcher
import pl.mobite.rocky.utils.inflate
import pl.mobite.rocky.utils.setVisibleOrGone


class SearchView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(R.layout.custom_search_view, true)

        clearButton.setOnClickListener { queryInput.setText("") }
        queryInput.addTextChangedListener(object : CustomTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                clearButton.setVisibleOrGone(queryInput.text.isNotEmpty())
            }
        })
    }

    fun setLoading(isLoading: Boolean) {
        queryInput.isEnabled = !isLoading
        queryInput.text.toString().let {queryText ->
            clearButton.setVisibleOrGone(queryText.isNotEmpty() && !isLoading)
            queryProgress.setVisibleOrGone(queryText.isNotEmpty() && isLoading)
        }
    }

    fun searchEvent() : Observable<String> {
        return RxTextView.editorActionEvents(queryInput) { action ->
            action.actionId() == EditorInfo.IME_ACTION_SEARCH && queryInput.text.toString().isNotBlank()}
                .map { queryInput.text.toString() }
    }
}