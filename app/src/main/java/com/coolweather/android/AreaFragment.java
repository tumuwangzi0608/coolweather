package com.coolweather.android;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    TextView titleText;
    Button backButton;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    private List<String> dataList = new ArrayList<>();

    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;

    //选中的省
    private Province selectProvince;
    //选中的市
    private City selectCity;
    //选中的县
    private County selectCounty;


    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvinces();//先加载省

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCiyts();
                }else if(currentLevel == LEVEL_CITY){
                    selectCity = cityList.get(position);
                    queryCounties();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCiyts();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
    }


    /**
     * 查询所有的省，先在数据库查，不存在在去服务器查
     */
    public void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//隐藏返回按钮
        provinceList = DataSupport.findAll(Province.class);//在数据库中查询所有的省
        if (provinceList.size() > 0) {
            dataList.clear();//清空datalist中的数据
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());//把省的名字取出来存在dataList中
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFormServer(address, "province");
        }
    }

    /**
     * 查询所有的市，先在数据库查，不存在在去服务器查
     */
    public void queryCiyts() {
        titleText.setText(selectProvince.getProvinceName());//设置标题
        backButton.setVisibility(View.VISIBLE);//显示返回按钮
        cityList = DataSupport.where("provinceid= ?",String.valueOf(selectProvince.getId())).find(City.class);//在数据库中查询所有的省
        if (cityList.size() > 0) {
            dataList.clear();//清空datalist中的数据
            for (City city : cityList) {
                dataList.add(city.getCityName());//把省的名字取出来存在dataList中
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectProvince.getProvinceCode();
            queryFormServer(address, "city");
        }
    }

    /**
     * 查询所有的县，先在数据库查，不存在在去服务器查
     */
    public void queryCounties() {
        titleText.setText(selectCity.getCityName());//设置标题
        backButton.setVisibility(View.VISIBLE);//显示返回按钮
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectCity.getId())).find(County.class);//在数据库中查询所有的省
        if (countyList.size() > 0) {
            dataList.clear();//清空datalist中的数据
            for (County county : countyList) {
                dataList.add(county.getCountyName());//把省的名字取出来存在dataList中
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            String address = "http://guolin.tech/api/china/" + selectProvince.getProvinceCode() +"/"+ selectCity.getCityCode();
            queryFormServer(address, "county");
        }
    }


    /**
     * 根据传入的请求地址和省市县类型从服务器请求数据
     */
    public void queryFormServer(String address, final String type) {

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AAA", "ssssssssssssssssssssss: ");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();//得到 返回体
                boolean result = false;//用于判断是否存储成功
                //根据传入的类型判断执行哪个存储
                if ("province".equals(type)) {
                     Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {

                     Utility.handleCityResponse(responseText, selectProvince.getId());
                } else if ("county".equals(type)) {

                     Utility.handleCountyResponse(responseText, selectCity.getId());
                }
            }
        });
    }
}
