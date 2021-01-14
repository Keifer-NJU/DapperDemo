package com.ae86.mypractice01.service;

import com.ae86.mypractice01.mbg.model.PmsBrand;

import java.util.List;

/**
 * @author Keifer
 */
public interface PmsBrandService {
    List<PmsBrand> listAllBrand();

    int createBrand(PmsBrand brand);

    int updateBrand(Long id, PmsBrand brand);

    int deleteBrand(Long id);

    List<PmsBrand> listBrand(int pageNum, int pageSize);

    PmsBrand getBrand(Long id);
}
