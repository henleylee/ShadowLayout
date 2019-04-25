# ShadowLayout —— Android 自定义阴影视图，可以替换 CardView

Screenshot

| H                                | H                                     | H                                 |
|:--------------------------------:|:-------------------------------------:|:---------------------------------:|
|Change Radius                     |Change foreground                      |Change Corners                     |
|![](/screenshot/shadow_radius.gif)|![](/screenshot/shadow_foreground.gif) |![](/screenshot/shadow_corners.gif)|
|Change shadow color               |Change shadow margin                   |Demo                               |
|![](/screenshot/shadow_color.gif) |![](/screenshot/shadow_margin_hide.gif)|![](/screenshot/shadow_demo.gif)   |

## Download ##
### Gradle ###
```gradle
dependencies {
    implementation 'com.henley.android:shadowlayout:1.0.1'
}
```

### APK Demo ###

Download [APK-Demo](https://github.com/HenleyLee/ShadowLayout/raw/master/app/app-release.apk)

Usages

```xml
<com.henley.shadowlayout.ShadowLayout
    android:id="@+id/shadow_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="12dp"
    android:elevation="10dp"
    android:foreground="?attr/selectableItemBackground"
    android:padding="10dp"
    app:cornerRadius="10dp"
    app:shadowMargin="10dp"
    app:shadowRadius="10dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Google Developer Days Europe 2017 took place in Krakow, Poland. In this playlist, you can find all the recorded sessions from the event, across all tracks (Develop on Mobile, Mobile Web, Beyond Mobile, and Android)."/>
</com.henley.shadowlayout.ShadowLayout>
```

Attribute

| Attribute          | Description                                       |
|--------------------|---------------------------------------------------|
| android:foreground | The drawable used as the foreground of this View. |
| foregroundColor    | The foreground color.                             |
| backgroundColor    | The background color.                             |
| shadowMargin       | The shadow margin in pixels.                      |
| shadowMarginTop    | The top shadow margin in pixels.                  |
| shadowMarginLeft   | The left shadow margin in pixels.                 |
| shadowMarginRight  | The right shadow margin in pixels.                |
| shadowMarginBottom | The bottom shadow margin in pixels.               |
| cornerRadius       | The corner radius in pixels.                      |
| cornerRadiusTL     | The top-left corner radius in pixels.             |
| cornerRadiusTR     | The top-right corner radius in pixels.            |
| cornerRadiusBL     | The bottom-left corner radius in pixels.          |
| cornerRadiusBR     | The bottom-right corner radius in pixels.         |
| shadowColor        | The shadow color.                                 |
| shadowDx           | The shadow dx in pixels.                          |
| shadowDy           | The shadow dy in pixels.                          |
| shadowRadius       | The shadow radius in pixels.                      |

