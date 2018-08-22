package com.example.cspy.floweranalysis.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DongtaiList {
    private List<Dongtai> dongtaiList;

    public DongtaiList() {
        this(null);
    }

    public DongtaiList(List<Dongtai> list) {
        if (list != null) {
            this.dongtaiList = list;
        } else {
            dongtaiList = new ArrayList<>();
        }
    }

    public List<Dongtai> getDongtaiList() {
        return dongtaiList;
    }


    public void setDongtaiList(DongtaiList dongtaiList) {
        this.dongtaiList = dongtaiList.getDongtaiList();
    }

    public void setDongtaiList(List<Dongtai> dongtaiList) {
        this.dongtaiList = dongtaiList;
    }

    public List<Dongtai> getDongtaiListByUID(String userid) {
        List<Dongtai> userDongtaiList = new ArrayList<>();
        for (Dongtai dongtai : dongtaiList) {
            if (dongtai.getUserId().equals(userid)) {
                userDongtaiList.add(dongtai);
            }
        }
        return userDongtaiList;
    }

    public List<Dongtai> addNewAndRemoveDeleted(DongtaiList list) {
        List<Dongtai> newDongtaiList = new ArrayList<>();

        //删除已经被删除的动态
        Iterator<Dongtai> iterator = dongtaiList.iterator();
        while (iterator.hasNext()) {
            Dongtai dongtai = iterator.next();
            if (!list.isExisted(dongtai)) {
                iterator.remove();
            }
        }
//        for (Dongtai dongtai : dongtaiList) {
//            if (!list.isExisted(dongtai)) {
//                dongtaiList.remove(dongtai);
//            }
//        }

        //添加新的动态
        for (Dongtai dongtai : list.getDongtaiList()) {
            if (!isExisted(dongtai)) {
                newDongtaiList.add(dongtai);
            }
        }
        dongtaiList.addAll(newDongtaiList);

        return newDongtaiList;
    }

    public boolean isExisted(Dongtai dt) {
        if (dongtaiList.size() < 1) {
            return false;
        }

        for (Dongtai dongtai : dongtaiList) {
            if (dongtai.getDongtaiId().equals(dt.getDongtaiId())) {
                return true;
            }
        }
        return false;
    }

    public void reverse() {
        Collections.reverse(dongtaiList);
    }


    public DongtaiList clone() {
        return new DongtaiList(new ArrayList<Dongtai>(dongtaiList));
    }
}
