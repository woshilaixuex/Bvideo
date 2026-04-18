package com.elyric.bredio.view.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import com.elyric.bredio.R
import com.elyric.bredio.databinding.FragmentHomeBinding
import com.elyric.bredio.view.component.fragment.BaseFragment
import com.elyric.domain.model.Video


class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VideoAdapter
    val videoViewModel: VideoViewModel by viewModels  {
        VideoViewModel.Factory
    }

    override fun initViews() {
        super.initViews()
        adapter = VideoAdapter()
//        binding.rvHome.layoutManager = GridLayoutManager(requireContext(), 2)
//        binding.rvHome.adapter = adapter
    }

    override fun initDatum() {
        super.initDatum()
        adapter.updateVideos(buildMockVideos())
    }

    override fun initListeners() {
        super.initListeners()
        binding.searchBarView.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun buildMockVideos(): List<Video> {
        return listOf(
            Video(
                id = "1",
                title = "从零实现类 B 站首页视频卡片布局",
                description = "首页双列推荐流布局演示",
                coverUrl = "https://picsum.photos/id/1015/800/450",
                playUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            Video(
                id = "2",
                title = "Kotlin 开发安卓播放器控制层的思路拆解",
                description = "播放器控制器封装",
                coverUrl = "https://picsum.photos/id/1025/800/450",
                playUrl = "https://www.w3schools.com/html/movie.mp4"
            ),
            Video(
                id = "3",
                title = "RecyclerView 双列视频流怎么做得更像真实产品",
                description = "双列 feed 流布局",
                coverUrl = "https://picsum.photos/id/1043/800/450",
                playUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            Video(
                id = "4",
                title = "Media3 和 ExoPlayer 在项目里的基本接入方式",
                description = "Media3 基础接入",
                coverUrl = "https://picsum.photos/id/1060/800/450",
                playUrl = "https://www.w3schools.com/html/movie.mp4"
            ),
            Video(
                id = "5",
                title = "首页搜索条、推荐流、点击跳转的完整串联",
                description = "首页交互串联",
                coverUrl = "https://picsum.photos/id/1067/800/450",
                playUrl = "https://www.w3schools.com/html/mov_bbb.mp4"
            ),
            Video(
                id = "6",
                title = "校招项目里如何把视频播放器讲出亮点",
                description = "项目包装思路",
                coverUrl = "https://picsum.photos/id/1074/800/450",
                playUrl = "https://www.w3schools.com/html/movie.mp4"
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {

            }
    }
}
