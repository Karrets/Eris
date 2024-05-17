# Eris
A very simplistic (for now) android only (for now?) media compression and conversion tool using the
Material You UI framework. Written in Kotlin (for reasons explained below) for my final project in
Mobile Applications Development @ WCTC. The app uses both MDC and Jetpack compose, but I would like
to migrate everything to Jetpack as it seems as though MDC may be being fazed out, and is overall
more difficult to work with.

## Appendix

|                                                                  | [React Native](#react-native) | [Kotlin](#kotlin) | [Flutter](#flutter) | [Xamarin](#xamarin) |
|------------------------------------------------------------------|-------------------------------|-------------------|---------------------|---------------------|
| [FFmpegKit](https://github.com/arthenica/ffmpeg-kit)</br>Support | 10                            | 10                | 10                  | 0                   |
| Popularity                                                       | 10                            | 8                 | 8                   | 5                   |
| Familiarity                                                      | 4                             | 8                 | 2                   | 7                   |
| Non-Web                                                          | 0                             | 5                 | 5                   | 5                   |
| Hot-Reload                                                       | 10                            | 8                 | 9                   | 3                   |
| Total / 45                                                       | 34                            | 39                | 34                  | 20                  |

## Overview

While react native and kotlin are both suitable for the project, my general disdain for working with
web frameworks (and the web as a whole), in addition to my frustration with the increase of web apps
replacing native programs (and the associated decrease in speed and reliability), I decided to use
kotlin for this app. While porting the app to iOS would likely be a challenge, I had no intention of
doing so, so this was a moot point for me. Overall I just wanted a fully native android app which
could compress and convert videos.

Additionally, my familiarity with kotlin (and java) would be of great help as I would be in an
environment I was much more comfortable with, which I knew would help speed up development time.
At first I was using Android MDC to design the app, but the it came to my attention that not all
Material 3 (aka Material You) components were available (or even planned for future implementation),
and as such, some parts of the app use Jetpack Compose, which I found to be more enjoyable to use
anyway.

## React Native

- Strengths:
    - Strong community support
    - Extremely popular
    - Great hot-reload
    - Full FFmpegKit compatibility
- Weaknesses
    - Primarily web-based, which can impact performance
    - My own poor familiarity with React Native as a platform.

## Kotlin

- Strengths
    - Solid performance across all categories
    - Great performance, and fully native on Android
    - Familiar platform, adjacent to one of my most well know programming languages
    - Full FFmpegKit compatibility
- Weaknesses
    - Slightly less popular than React Native
    - Hot-reloading, while good, isn't as seamless as React Native or Flutter.

## Flutter

- Strengths
    - Fast hot-reloading
    - Good performance
    - Growing popularity.
    - Full FFmpegKit compatibility
- Weaknesses
    - Relatively new with a smaller community compared to React Native and Kotlin
    - Requires learning Dart

## Xamarin

- Strengths
  - A language I have a lot of familiarity with
  - Fair performance
- Weaknesses
  - Zero FFMpegKit support (Essentially a deal breaker)
  - Hot reloading is less efficient (and it's never worked for me)
  - Declining in popularity in recent years
  - Personally, I found XAML and its UI tooling to be poorly documented and clunky.