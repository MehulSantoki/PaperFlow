package com.paperflow.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookDetailResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("book")
    @Expose
    private Book book;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public class Book {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("bookname")
        @Expose
        private String bookname;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("bookdesc")
        @Expose
        private String bookdesc;
        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBookname() {
            return bookname;
        }

        public void setBookname(String bookname) {
            this.bookname = bookname;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBookdesc() {
            return bookdesc;
        }

        public void setBookdesc(String bookdesc) {
            this.bookdesc = bookdesc;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

    }

}