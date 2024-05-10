package tom1tom.softether.benri_tool.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import tom1tom.softether.benri_tool.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeFragmentController controller;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // バインディングを初期化し、ルートビューを取得
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // HomeFragmentControllerを初期化し、機能を開始
        controller = new HomeFragmentController(requireContext(), root);
        controller.initialize();

        return root;
    }
}
