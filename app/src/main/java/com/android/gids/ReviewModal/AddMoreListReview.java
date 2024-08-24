package com.android.gids.ReviewModal;

import com.android.gids.FormStructureModal;

import java.util.List;

public class AddMoreListReview {

    List<List<FormStructureModalReview>> addMoreList;
    public String id;

    public List<List<FormStructureModalReview>> getAddMoreList() {
        return addMoreList;
    }

    public void setAddMoreList(List<List<FormStructureModalReview>> addMoreList) {
        this.addMoreList = addMoreList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
