package ru.clevertec.utils;

import ru.clevertec.entity.NewsEntity;

public class NewsEntityFactory {

    public static NewsEntity createNews(String title, String text, String username) {
        NewsEntity news = new NewsEntity();
        news.setTitle(title);
        news.setText(text);
        news.setUsername(username);
        return news;
    }

    public static NewsEntity createDefaultNews() {
        return createNews("Default Title", "Default Text", "Default Author");
    }
}

