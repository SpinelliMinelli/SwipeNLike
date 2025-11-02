package com.mobiledev.swipedb

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobiledev.swipedb.adapters.CardStackAdapter
import com.mobiledev.swipedb.adapters.RecyclerViewAdapter
import com.mobiledev.swipedb.database.models.ImageCard
import com.mobiledev.swipedb.databinding.ActivityMainBinding
import com.mobiledev.swipedb.viewModels.MainViewModel
import com.yuyakaido.android.cardstackview.*

class MainActivity : AppCompatActivity(), CardStackListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var searchButton: Button
    private lateinit var appLoadImage: ImageView
    private lateinit var editTextSearchTerm: EditText
    private lateinit var buttonContainer: RelativeLayout
    private lateinit var searchLinearLayout: LinearLayout
    private lateinit var cardStackView: CardStackView
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigation: BottomNavigationView
    private val cardStackLayoutManager by lazy { CardStackLayoutManager(this, this) }
    private val cardStackAdapter by lazy { CardStackAdapter() }
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigation = findViewById(R.id.navigation)
        cardStackView = findViewById(R.id.cardStackView)
        searchLinearLayout = findViewById(R.id.searchLinearLayout)
        buttonContainer = findViewById(R.id.buttonContainer)
        editTextSearchTerm = findViewById(R.id.editTextSearchTerm)
        appLoadImage = findViewById(R.id.appLoadImage)
        searchButton = findViewById(R.id.searchButton)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewAdapter = RecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter

        // Setup functions
        setupNavigation()
        setupCardStackView()
        setupButtons()
        setupSearchView()
        setupRecyclerView()
        observeLiveData()
    }

    private fun setupNavigation() {
        navigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.search_swipe -> {
                    searchLinearLayout.visibility = View.VISIBLE
                    cardStackView.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                    buttonContainer.visibility = View.VISIBLE

                    if (editTextSearchTerm.text.toString().isEmpty()) {
                        appLoadImage.visibility = View.VISIBLE
                        buttonContainer.visibility = View.INVISIBLE
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.my_likes -> {
                    searchLinearLayout.visibility = View.INVISIBLE
                    cardStackView.visibility = View.INVISIBLE
                    recyclerView.layoutManager?.smoothScrollToPosition(recyclerView, null, 0)
                    recyclerView.visibility = View.VISIBLE
                    buttonContainer.visibility = View.INVISIBLE
                    appLoadImage.visibility = View.INVISIBLE
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setupCardStackView() {
        cardStackLayoutManager.setStackFrom(StackFrom.None)
        cardStackLayoutManager.setVisibleCount(3)
        cardStackLayoutManager.setTranslationInterval(8.0f)
        cardStackLayoutManager.setScaleInterval(0.95f)
        cardStackLayoutManager.setSwipeThreshold(0.3f)
        cardStackLayoutManager.setMaxDegree(20.0f)
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL)
        cardStackLayoutManager.setCanScrollHorizontal(true)
        cardStackLayoutManager.setCanScrollVertical(true)
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        cardStackLayoutManager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = cardStackLayoutManager
        cardStackView.adapter = cardStackAdapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun setupButtons() {
        val skip = findViewById<View>(R.id.skipButton)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val like = findViewById<View>(R.id.likeButton)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .build()
            cardStackLayoutManager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = findViewById<View>(R.id.rewindButton)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(200)
                .setInterpolator(DecelerateInterpolator())
                .build()
            cardStackLayoutManager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewAdapter.appendLikes(viewModel.getLikes())
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun setupSearchView() {
        searchButton.setOnClickListener {
            if (editTextSearchTerm.text.toString().isNotEmpty()) {
                viewModel.searchImages(editTextSearchTerm.text.toString())

                appLoadImage.visibility = View.INVISIBLE
                cardStackView.visibility = View.VISIBLE
                buttonContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun observeLiveData() {
        viewModel.liveData.observe(this, Observer {
            val imageCards = ArrayList<ImageCard>()

            it.forEach { image ->
                val imageCard = ImageCard(imageId = image.id, url = image.urls.small)
                imageCards.add(imageCard)
            }

            cardStackAdapter.setCards(imageCards)
        })
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${cardStackLayoutManager.topPosition}, d = $direction")
        if (cardStackLayoutManager.topPosition == cardStackAdapter.itemCount - 5) {
            // search for more images if desired
        }

        if (direction == Direction.Right) {
            val imageCard = cardStackAdapter.getCards()[cardStackLayoutManager.topPosition - 1]
            viewModel.addLike(imageCard)
            recyclerViewAdapter.appendLike(imageCard)
        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${cardStackLayoutManager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${cardStackLayoutManager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.itemName)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.itemName)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }
}
