package tom1tom.softether.benri_tool.ui.work;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import tom1tom.softether.benri_tool.databinding.FragmentWorkBinding;

public class WorkFragment extends Fragment {

    private FragmentWorkBinding binding;
    private WorkFragmentController controller;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // バインディングを初期化し、ルートビューを取得
        binding = FragmentWorkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // WorkFragmentControllerを初期化し、機能を開始
        controller = new WorkFragmentController(requireContext(), root);
        controller.initialize();

        return root;
    }
}
