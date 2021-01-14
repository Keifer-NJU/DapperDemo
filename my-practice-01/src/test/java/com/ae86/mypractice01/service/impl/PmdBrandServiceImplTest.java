package com.ae86.mypractice01.service.impl;

import com.ae86.mypractice01.mbg.mapper.PmsBrandMapper;
import com.ae86.mypractice01.mbg.model.PmsBrand;
import com.ae86.mypractice01.service.PmsBrandService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PmdBrandServiceImplTest {

    @MockBean
    private PmsBrandMapper brandMapper;

    private PmsBrand pmsBrandExpected;

    @Autowired
    private PmsBrandService PmsBrandService;

    @Before
    public void setup() {
        pmsBrandExpected = new PmsBrand();
        pmsBrandExpected.setName("华为");
    }

    @Test
    public void getBrand() {
//        brandMapper = mock(PmsBrandMapper.class);
        when(brandMapper.selectByPrimaryKey(any(Long.class))).thenReturn(pmsBrandExpected);
        PmsBrand pmsBrandActual = PmsBrandService.getBrand(1l);
//        assertThat(pmsBrand).isNotNull();
        Assert.assertEquals(pmsBrandExpected, pmsBrandActual);
    }

}