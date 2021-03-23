# Martian Weather

<!--- Replace <OWNER> with your Github Username and <REPOSITORY> with the name of your repository. -->
<!--- You can find both of these in the url bar when you open your repository in github. -->
![Workflow result](https://github.com/rcacurs/MartianWeather/workflows/Check/badge.svg)


## :scroll: Description
This is app is created for #AndroidDevChallenge Week #4. It is a weather reporting application, 
but to make things more interesting I built the application that reports weather on Mars! 
I uses data from NASA's  InSight Mars lander. The lander is located near the equator at location
named - Elysium Planitia. The data is available through the  
[InSight: Mars Weather Service API](https://api.nasa.gov/assets/insight/InSight%20Weather%20API%20Documentation.pdf)

Unfortunately though currently the landers solar pannels are covered with dust and it is
placed in power conservation mode and not all data is available.
Currently only atmospheric pressure data is available. Hopefully all data will be available soon enough! For demonstration purposes missing data is generated.


## :bulb: Motivation and Context
<!--- Optionally point readers to interesting parts of your submission. -->
<!--- What are you especially proud of? -->

My main motiviation for participating in these challenges was for updating my knowledge on android development. I was starting to get back into Android development and just in time the Jeptack compose went into Beta, and I decided that this is a very great time to learn this toolkit!

I had a really good time learning Jetpack Compose. Building dynamic UI, was quite enjoyable, and implementing various ideas was very intuitive. 

![](results/screenshot_5.gif)

I am happy, that I manage to draw custom composable for Compass, and also animate the compass needle, so it would look more interesting and would react to movement.

I also managed to implement SwipeToRefreshLayout like behaviour for refreshing the data. Learned a lot about gesture detection in Compose!

![](results/screenshot6.gif)


## :camera_flash: Screenshots
<!-- You can add more screenshots here if you like -->
<img src="results/screenshot_1.png" width="260">&emsp;<img src="results/screenshot_2.png" width="260">

The weather cards can be expanded, to show more detail. Also created swipe to refresh like behaviour for refreshing weather data list.

<img src="results/screenshot_3.png" width="260">&emsp;<img src="results/screenshot_4.png" width="260">


## License
```
Copyright 2020 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```