![Downtown Logo](https://dl.dropboxusercontent.com/u/4888041/downtown-app-logo.png)

# Downtown - City guide android app
---------------------------------------------------------------------------------------------------

  NOTICE: This app has been recently migrated from eclipse to android studio and from actionbarsherlock 
  to actionbarcompat and has been not tested properly thus it remains buggy.
  Also its admin web app is deprecated and needs to be rewritten.
  
  So the entire downtown application is considered retired and not in development.  
  
---------------------------------------------------------------------------------------------------

## Description
_Downtown_ is a data driven -reference- city guide app. 

It is a very informative App for Tourists, Travelers and local residents of City.

Companies are organized in a category-subcategory manner and by location.
All data is stored in a local database.
Most of its data is configurable through http/xml API. A web admin panel has been built to that end.


## Feature list

* Unlimited categories - subcategories with icons.
* Keyword based **search** function with autocomplete.
* Android maps v2 and map utils.
* Show business locations in Map
* Extracts user location and shows nearby points of interest (filtered by category).
* Clustering of multiple locations.
* Calculates nearest route with car, foot or bus.
* Database storage
* Supports shipping with prepopulated database.
* Async tasks for all database ops.
* Background async update service with notifications.
* Favourites.
* Multiple images per buisiness, downloadble on demand
* Google's ActionBarCompat, ensure action bar availability for lower version device
* Supports advertisments and banners throughout the app. 
* Make phone call to business or visit website from URL link in webview.
* Share helper to share on all available services. 
* Static pages.
* Holo custom theme.
* Some custom widgets.
* Sliding up panel.
* Image downloading and caching
 
## Screenshots
![Screenshot1](https://dl.dropboxusercontent.com/u/4888041/downtown-shots/1.png)
![Screenshot2](https://dl.dropboxusercontent.com/u/4888041/downtown-shots/2.png)
![Screenshot3](https://dl.dropboxusercontent.com/u/4888041/downtown-shots/3.png)
![Screenshot4](https://dl.dropboxusercontent.com/u/4888041/downtown-shots/4.png)
![Screenshot5](https://dl.dropboxusercontent.com/u/4888041/downtown-shots/5.png)
![Screenshot6](https://dl.dropboxusercontent.com/u/4888041/downtown-shots/6.png)


## Dependencies
* [Play Services](https://developer.android.com/google/play-services/index.html)
* [Support libs with Actionbar Compat](https://developer.android.com/tools/support-library/features.html)
* [Maps Utils](http://googlemaps.github.io/android-maps-utils/): open-source library contains classes that are useful for a wide range of applications using the [Google Maps Android API](http://developer.android.com/google/play-services/maps.html).
* [Android Sliding Up Panel](https://github.com/umano/AndroidSlidingUpPanel): provides a simple way to add a draggable sliding up panel.
* [Picasso](https://github.com/square/picasso): powerful image downloading and caching library.
* [Sqlite Asset Helper](https://github.com/jgilfelt/android-sqlite-asset-helper): helper class to manage database creation and version management using an application's raw asset files.

## Contributing

1. Fork it ( https://github.com/dklisiaris/downtown/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create a new Pull Request