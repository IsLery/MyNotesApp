package com.islery.mynotesapp.ui.list;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.islery.mynotesapp.MainActivity;
import com.islery.mynotesapp.R;
import com.islery.mynotesapp.data.models.Note;
import com.islery.mynotesapp.databinding.FragmentNoteListBinding;
import com.islery.mynotesapp.ui.note.NoteFragment;




/**
 * A simple {@link Fragment} subclass.
 */
public class NoteListFragment extends Fragment implements NoteListAdapter.OnNoteClickListener, View.OnClickListener {
    private FragmentNoteListBinding binding;
    private NoteListViewModel viewModel;
    private NoteListAdapter adapter;
    private static final String TAG = "NoteListFragment";

    public NoteListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu,menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               viewModel.getSearchResult(query);
                Log.d(TAG, "onQueryTextSubmit: "+query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        View closeBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeBtn.setOnClickListener(v ->
                {
                    searchView.setQuery("", false);
                    viewModel.clearSearch();
                }
        );
        searchView.setOnSearchClickListener(v -> searchView.setQuery(viewModel.getSearchQuery(),false));
        boolean isAscSort = viewModel.isCurrentOrderAsc();
        boolean isAlphaSort = viewModel.isCurrentSortAlpha();
        if (isAlphaSort) {
            menu.findItem(R.id.alpha_sort_desc).setVisible(isAscSort);
            menu.findItem(R.id.alpha_sort_asc).setVisible(!isAscSort);
            menu.findItem(R.id.date_sort_desc).setVisible(!isAlphaSort);
            menu.findItem(R.id.date_sort_asc).setVisible(isAlphaSort);
        }else{
            menu.findItem(R.id.alpha_sort_desc).setVisible(isAlphaSort);
            menu.findItem(R.id.alpha_sort_asc).setVisible(!isAlphaSort);
            menu.findItem(R.id.date_sort_desc).setVisible(!isAscSort);
            menu.findItem(R.id.date_sort_asc).setVisible(isAscSort);
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pick_date_item:
                showTimePickerDialog();
                break;
            case R.id.alpha_sort_asc:
            case R.id.alpha_sort_desc:
                viewModel.setAlphaSort();
                getActivity().invalidateOptionsMenu();
                break;
            case R.id.date_sort_asc:
            case R.id.date_sort_desc:
                viewModel.setDateSort();
                getActivity().invalidateOptionsMenu();

        }
        return super.onOptionsItemSelected(item);
    }


    private void showTimePickerDialog() {


        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTitleText("Select date range");

        Pair<Long, Long> oldSelection = viewModel.getDateRange();
        if (oldSelection != null){
            builder.setSelection(oldSelection);
        }

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            if (selection != null) {
              viewModel.setDateRange(selection);
            }
        });
        picker.addOnNegativeButtonClickListener(v -> viewModel.clearDateRange());
        picker.show(getParentFragmentManager(), "picker");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoteListBinding.inflate(inflater,container,false);
        ((MainActivity)getActivity()).setSupportActionBar(binding.toolbar);
        initRv();
        viewModel = new ViewModelProvider(requireActivity()).get(NoteListViewModel.class);

        viewModel.getNoteLiveData().observe(getViewLifecycleOwner(), notes -> {
            if (notes != null) {
                adapter.updateRv(notes);
                binding.emptyListTxt.setVisibility(View.GONE);
            }else {
                binding.emptyListTxt.setVisibility(View.VISIBLE);
            }
        });

        binding.addNoteBtn.setOnClickListener(this);
        return binding.getRoot();
    }


    private void initRv(){
        adapter = new NoteListAdapter(this);
        binding.notesRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.notesRv.setAdapter(adapter);
        binding.notesRv.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.deleteNote(adapter.getItem(viewHolder.getAdapterPosition()));
            }
        });
        helper.attachToRecyclerView(binding.notesRv);

        }


    @Override
    public void onNoteClick(int pos) {
        Note note = adapter.getItem(pos);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.container, NoteFragment.newInstance(note))
                .addToBackStack("note_fragment")
                .commit();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNoteBtn){
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, NoteFragment.newInstance(null))
                    .addToBackStack("note_fragment")
                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
