package com.colman.mobilePostsApp.modules

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.colman.mobilePostsApp.databinding.FragmentBookSearchBinding
import com.colman.mobilePostsApp.data.book.BookViewModel

class BookSearchFragment : Fragment() {

    private var _binding: FragmentBookSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BookViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[BookViewModel::class.java]

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        binding.bookSearchInput.setAdapter(adapter)

        binding.bookSearchInput.setOnItemClickListener { _, _, position, _ ->
            val selectedBook = adapter.getItem(position)
            binding.selectedBookTextView.text = "Selected Book: $selectedBook"
        }

        // Add TextWatcher correctly
        binding.bookSearchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length > 2) { // Start search after 3+ characters
                    viewModel.searchBooks(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // ✅ Observe `bookTitles` correctly
        viewModel.bookTitles.observe(viewLifecycleOwner) { bookTitles ->
            adapter.clear()
            adapter.addAll(bookTitles)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
