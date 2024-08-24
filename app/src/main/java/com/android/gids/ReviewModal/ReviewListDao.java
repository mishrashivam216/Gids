package com.android.gids.ReviewModal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface ReviewListDao {

    @Insert
    void insert(ReviewListEntity reviewListEntity);

    @Update
    void update(ReviewListEntity reviewListEntity);

    @Delete
    void delete(ReviewListEntity reviewListEntity);

    @Query("SELECT * FROM review_list")
    List<ReviewListEntity> getAllReviews();

    @Query("SELECT * FROM review_list WHERE id = :id")
    ReviewListEntity getReviewById(int id);

    @Query("SELECT * FROM review_list WHERE recId = :recId")
    List<ReviewListEntity> getReviewsByRecId(String recId);

    @Query("DELETE FROM review_list")
    void deleteAllReviews();

    @Query("DELETE FROM review_list WHERE recId = :recId")
    void deleteReviewsByRecId(String recId);
}
