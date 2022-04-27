package com.example.firebasesample.ui.details.anime

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.RatingBar
import android.widget.Space
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.example.firebasesample.R
import com.example.firebasesample.SampleScreen
import com.example.firebasesample.data.models.*
import com.example.firebasesample.data.network.MalApiStatus
import com.example.firebasesample.ui.overview.AnimeItem
import com.example.firebasesample.ui.overview.AnimePoster
import com.example.firebasesample.ui.overview.AnimeRow
import com.example.firebasesample.utli.Constants
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.exp

@Composable
@ExperimentalMaterialApi
fun AnimeDetailsBody(
    anime: Anime,
    user: User,
    isFavorited: Boolean,
    isWatched: Boolean,
    onClickFavorite: (Anime) -> Unit,
    onClickRemoveFavorite: (String) -> Unit,
    onClickWatched: (Anime) -> Unit,
    onClickRemoveWatched: (String) -> Unit,
    status: MalApiStatus,
    onClickBack: () -> Unit,
    onClickAddReview: (String, Int) -> Unit,
    userReviews: List<AnimeReview>,
    onClickRelatedAnime: (AnimeRelated) -> Unit,
    onClickRecommended: (AnimeRecommendation) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(anime.title) },
                navigationIcon = {
                    IconButton(onClick = { onClickBack() }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Back")
                    }
                }
            )
        },
    ) {
        LazyColumn {
            item {
                AnimeDetail(
                    anime = anime,
                    isFavorited = isFavorited,
                    isWatched = isWatched,
                    onClickFavorite = onClickFavorite,
                    onClickRemoveFavorite = onClickRemoveFavorite,
                    onClickWatched = onClickWatched,
                    onClickRemoveWatched = onClickRemoveWatched,
                    status = status
                )
            }

            // Related anime row list
            item {
                AnimeRelatedRow(title = "Related Animes", animes = anime.related_anime, onClickAnime = onClickRelatedAnime)
            }
            // Recommend Anime
            item {
                AnimeRecommendedRow(title = "Recommended Animes", animes = anime.recommendations, onClickRecommended = onClickRecommended)
            }

            item {
                Divider()
                CreateReview(
                    modifier = Modifier.padding(16.dp),
                    onClickAddReview = onClickAddReview,
                    numReviews = userReviews.size,
                    user = user
                )
            }
            items(userReviews) { review ->
                Review(review)
            }
            item {
                Spacer(modifier = Modifier.padding(140.dp)) // empty space below comment space
            }
        }
    }
}

@Composable
fun AnimeRecommendedRow(title: String, animes: List<AnimeRecommendation>, onClickRecommended: (AnimeRecommendation) -> Unit) {
    Spacer(modifier = Modifier.padding(4.dp))
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title,
                modifier = Modifier.padding(start = 12.dp),
                fontSize = 20.sp,
                fontFamily = Constants.robotoFamily,
                fontWeight = FontWeight.Normal
            )
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            contentPadding = PaddingValues(start = 2.dp, end = 2.dp)
        ) {
            items(animes) { anime ->
                AnimeRecommendedItem(anime, onClickRecommended)
            }
        }
    }
}

@Composable
fun AnimeRecommendedItem(animeRecommended: AnimeRecommendation, onClickRecommendedAnime: (AnimeRecommendation) -> Unit) {
    // TODO: Move this into viewmodel
    val context = LocalContext.current
    var rgb by rememberSaveable { mutableStateOf(ContextCompat.getColor(context, R.color.black)) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        coroutineScope.launch {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(animeRecommended.node.main_picture.medium) // demo link
                .build()
            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
            Palette.from(bitmap).generate { palette ->
                palette?.vibrantSwatch?.rgb?.let { colorValue ->
                    rgb = colorValue
                    Log.i("OverviewScreen", "Got egb: $rgb")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .width(130.dp)
            .fillMaxHeight()
            .clickable {
                onClickRecommendedAnime(animeRecommended)
            }
            .padding(8.dp)

    ) {
        AnimePoster(animeRecommended.node.main_picture.medium)
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = animeRecommended.node.title,
            fontSize = 12.sp, fontFamily = Constants.robotoFamily,
            fontWeight = FontWeight.Normal,
            color = Color(rgb),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AnimeRelatedRow(title: String, animes: List<AnimeRelated>, onClickAnime: (AnimeRelated) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = title,
                modifier = Modifier.padding(start = 12.dp),
                fontSize = 20.sp,
                fontFamily = Constants.robotoFamily,
                fontWeight = FontWeight.Normal
            )
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
        }
        Spacer(modifier = Modifier.padding(4.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            contentPadding = PaddingValues(start = 2.dp, end = 2.dp)
        ) {
            items(animes) { anime ->
                AnimeRelatedItem(anime, onClickAnime)
            }
        }
    }
}

@Composable
fun AnimeRelatedItem(animeRelated: AnimeRelated, onClickRelatedAnime: (AnimeRelated) -> Unit) {
    // TODO: Move this into viewmodel
    val context = LocalContext.current
    var rgb by rememberSaveable { mutableStateOf(ContextCompat.getColor(context, R.color.black)) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        coroutineScope.launch {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(animeRelated.node.main_picture.medium) // demo link
                .build()
            val result = (loader.execute(request) as SuccessResult).drawable
            val bitmap = (result as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
            Palette.from(bitmap).generate { palette ->
                palette?.vibrantSwatch?.rgb?.let { colorValue ->
                    rgb = colorValue
                    Log.i("OverviewScreen", "Got egb: $rgb")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .width(130.dp)
            .fillMaxHeight()
            .clickable {
                onClickRelatedAnime(animeRelated)
            }
            .padding(8.dp)

    ) {
        AnimePoster(animeRelated.node.main_picture.medium)
        Spacer(modifier = Modifier.padding(5.dp))
        Text(
            text = animeRelated.node.title,
            fontSize = 12.sp, fontFamily = Constants.robotoFamily,
            fontWeight = FontWeight.Normal,
            color = Color(rgb),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = animeRelated.relation_type_formatted,
            fontFamily = Constants.robotoFamily, fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun CreateReview(modifier: Modifier, onClickAddReview: (String, Int) -> Unit, numReviews: Int, user: User) {
    Column(modifier = modifier) {
        Text(text = "$numReviews Reviews", fontSize = 24.sp)
        Spacer(modifier = Modifier.padding(8.dp))
        
        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        Row {
            AsyncImage(
                model = user.profileImage,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.padding(8.dp))
            Column {
                Row {
                    Text(
                        text = user.username,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Add a comment...") }
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { focusManager.clearFocus() }
                    ) {
                        Text("CANCEL")
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                    Button(
                        onClick = {
                            onClickAddReview(text, 8)
                            text = ""
                            focusManager.clearFocus() },
                        enabled = text.isNotBlank()
                    ) {
                        Text("COMMENT")
                    }
                }
            }
        }
    }
    Divider(startIndent = 80.dp)
}

@Composable
fun Review(review: AnimeReview) {
    Row(modifier = Modifier.padding(16.dp)) {
        AsyncImage(
            model = review.authorData.profileImage,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Row {
                Text(
                    text = review.authorData.username,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    color = Color.Gray,
                    text = review.authorData.createdAt
                )
            }
            Spacer(modifier = Modifier.padding(2.dp))
            Text(text = review.authorData.review)
        }
    }
    Divider(startIndent = 80.dp)
}

@Composable
fun AnimeDetail(
    anime: Anime,
    isFavorited: Boolean,
    isWatched: Boolean,
    onClickFavorite: (Anime) -> Unit,
    onClickRemoveFavorite: (String) -> Unit,
    onClickWatched: (Anime) -> Unit,
    onClickRemoveWatched: (String) -> Unit,
    status: MalApiStatus
) {
    Column(modifier = Modifier.padding(16.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(anime.main_picture.medium)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .height(200.dp)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        AnimeHeading(
            anime = anime,
            isFavorited = isFavorited,
            isWatched = isWatched,
            onClickFavorite = onClickFavorite,
            onClickRemoveFavorite = onClickRemoveFavorite,
            onClickWatched = onClickWatched,
            onClickRemoveWatched = onClickRemoveWatched
        )
        FlowRow(
            mainAxisSpacing = 6.dp,
            crossAxisSpacing = 4.dp,
        ) {
            anime.genres.forEach { genre ->
                Text(genre.name, color = Color.Gray)
                Text("|", color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.padding(12.dp))
        AnimeSynopsis(synopsis = anime.synopsis)
    }
}

@Composable
fun AnimeHeading(
    anime: Anime,
    isFavorited: Boolean,
    isWatched: Boolean,
    onClickFavorite: (Anime) -> Unit,
    onClickRemoveFavorite: (String) -> Unit,
    onClickWatched: (Anime) -> Unit,
    onClickRemoveWatched: (String) -> Unit,
) {
    Row {
        Column(modifier = Modifier.weight(5f)) {
            Row {
                Text(anime.start_season.season.capitalize(), color = Color.Gray)
                Spacer(modifier = Modifier.padding(2.dp))
                Text(anime.start_season.year, color = Color.Gray)
            }
            Text(
                text = anime.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Icon(
                imageVector = if (isFavorited) { Icons.Default.Favorite} else { Icons.Default.FavoriteBorder },
                contentDescription = "Favorite",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        if (isFavorited) {
                            onClickRemoveFavorite(anime.id)
                        } else {
                            onClickFavorite(anime)
                        }
                    },
                tint = colorResource(R.color.favorite)
            )
        }
    }
}

@Composable
fun AnimeSynopsis(synopsis: String) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .animateContentSize()
    ) {
        Text(
            text = synopsis,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = if (!isExpanded) "Read more" else "Read less",
                modifier = Modifier
                    .clickable { isExpanded = !isExpanded }
                    .padding(8.dp),
                color =  colorResource(id = R.color.dark_blue)
            )
        }
    }
}