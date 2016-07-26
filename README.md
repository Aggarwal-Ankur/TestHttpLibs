A demo project to test various Http libs available in Android.

To use this demo, you need an API key (by signing up here : https://www.themoviedb.org/documentation/api )

Once you have the API key, place your API key in the following file :
%USERHOME%\.gradle\gradle.properties

The above gradle.properties should include a line like this :

MyMovieDbApiKey = "your-api-key"

-----

For the setup information and additional resources of each library, refer to :

HttpURLConnection:
https://developer.android.com/reference/java/net/HttpURLConnection.html

OK HTTP:
http://square.github.io/okhttp/
https://http2.github.io/faq/

Retrofit:
http://square.github.io/retrofit/
https://futurestud.io/blog/retrofit-2-how-to-add-query-parameters-to-every-request
https://speakerdeck.com/jakewharton/simple-http-with-retrofit-2-droidcon-nyc-2015

Volley:
http://www.technoburgh.com/android/android-studio-volley/
https://developer.android.com/training/volley/request.html
https://www.bignerdranch.com/blog/solving-the-android-image-loading-problem-volley-vs-picasso/


-----
Most of the code for the libraries used in this is available under Apache Software License 2.0 (See respective licenses and LICENSE.md)

-----

```bash
                           Volley                          Retrofit                     OK Http                       HttpUrlConnection
First setup                AOSP (Github checkout)          Maven (jar available)        Maven (jar available)         None
First setup time           30 min (documentation !)        2 min                        2 min                         None
APK Size (Base)            1318 KB                         1463 KB                      1423 KB                       1108 KB
Request Queue              Possible                        Possible but custom          No                            No
Request Prioritization     Possible but looks crappy       No                           No                            No
Thread Pool                Default = 4                     Default = unlimited          No                            No
Caching                    Default + Custom                custom                       Custom                        Not working for me
Cache Type                 RAM + Disk based                RAM + Disk based             RAM                           NA
Cancellation               Possible                        Only marks as cancelled      Only marks as cancelled       Only marks as cancelled
Image loading              Easy                            No (With Picasso)            No                            No
Batch response             Possible                        No                           No                            No
Cache Sizes                Customizable                    Customizable                 Customizable                  Not working for me
Retry Policy               Customizable                    Customizable                 Customizable                  No
Responses                  Default = Raw                   Parsed                       Raw                           Raw
URL Manipulation           No                              Yes                          No                            No

    
    