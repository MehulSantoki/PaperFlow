package com.paperflow.responses;


import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetUserBooksResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("userBooks")
    @Expose
    private ArrayList<UserBook> userBooks = null;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ArrayList<UserBook> getUserBooks() {
        return userBooks;
    }

    public void setUserBooks(ArrayList<UserBook> userBooks) {
        this.userBooks = userBooks;
    }



    public class UserBook {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("bookQRcode")
        @Expose
        private String bookQRcode;
        @SerializedName("bookid")
        @Expose
        private String bookid;
        @SerializedName("bookname")
        @Expose
        private String bookname;
        @SerializedName("bookdesc")
        @Expose
        private String bookdesc;
        @SerializedName("thumbnail")
        @Expose
        private String thumbnail;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;
        @SerializedName("__v")
        @Expose
        private Integer v;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getBookQRcode() {
            return bookQRcode;
        }

        public void setBookQRcode(String bookQRcode) {
            this.bookQRcode = bookQRcode;
        }

        public String getBookid() {
            return bookid;
        }

        public void setBookid(String bookid) {
            this.bookid = bookid;
        }

        public String getBookname() {
            return bookname;
        }

        public void setBookname(String bookname) {
            this.bookname = bookname;
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

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

    }

}