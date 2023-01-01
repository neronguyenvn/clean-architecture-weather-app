# Weather Clean Architecture [![Codacy Badge](https://app.codacy.com/project/badge/Grade/02bec68464c740c6ba47f49622d6e099)](https://www.codacy.com/gh/hungnd-vnse/Weather-Clean-Architecture/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=hungnd-vnse/Weather-Clean-Architecture&amp;utm_campaign=Badge_Grade)

A weather android app demonstrates **Clean Architecture** and is written in **Kotlin**.  
The Ui is only built with **Jetpack Compose**. But I tend to build it with **XML** as the new year
come.

## Introduction

I try to follow **Clean Architecture** guide recommended architecture for building robust,
high-quality apps.

* User Interface built with [Jetpack Compose](https://developer.android.com/jetpack/compose)
* A single-activity architecture
* A **presentation layer** that contains a Compose screen (View) and a **ViewModel** per screen (or
  feature).
* Reactive UIs using **Flow** and **Coroutines** for asynchronous operations.
* A **data layer** with repositories and data sources (local using Room and a api service).
* Dependency injection
  using [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

## Screenshot

![Home screen](/screenshot/home_screen.jpg)