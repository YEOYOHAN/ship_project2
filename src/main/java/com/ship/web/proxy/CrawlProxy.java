package com.ship.web.proxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("crawler")
@Lazy
public class CrawlProxy extends Proxy{
	@Autowired Box<HashMap<String, String>> box;
	@Autowired Trunk<String> trunk;
	
	public ArrayList<HashMap<String, String>> opggCrawling(String lolname){
		box.clear();
		try {
			final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
			String url = "http://www.op.gg/summoner/userName="+lolname;
			Connection.Response page =
					Jsoup.connect(url)
					.method(Connection.Method.GET)
					.userAgent(USER_AGENT)
					.execute();
			Document temp = page.parse();
			Elements photo = temp.select("img.ChampionImage");
			Elements summonername = temp.select("span.Name");
			Elements tier = temp.select("div.TierRank");
			Elements rate = temp.select("div.TierInfo");
			Elements most = temp.select("div.MostChampionContent");
			Elements position = temp.select("td.PositionStats");
			HashMap<String, String> map = null;
				map = new HashMap<>();
				map.put("summonername", summonername.get(0).text());
				map.put("tier", tier.get(0).text());
				map.put("rate", rate.get(0).text());
				map.put("most", most.get(0).text());
				map.put("position", position.get(0).text());
				map.put("photo", photo.get(0).select("img").attr("src"));
				box.add(map);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return box.get();
	}
	
	public List<Map<String, String>> crawlFutMatch(int page){
		final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
		String url = "https://map.naver.com/v5/api/search?caller=pcweb&query=풋살장&type=all&page="+page+"&displayCount=100&isPlaceRecommendationReplace=true&lang=ko";
		List<Map<String, String>> list = new ArrayList<>();
		JSONObject json = null;
		Map<String, String> map = null;
		try {
			Connection.Response html = Jsoup.connect(url)
			.method(Connection.Method.GET)
			.userAgent(USER_AGENT)
			.ignoreContentType(true)
			.execute();
			//System.out.println(html.toString());
			json = new JSONObject(html.parse().select("body").text());
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONArray jsonArr = json.getJSONObject("result")
				.getJSONObject("place")
				.getJSONArray("list");
		for(int i = 0; i < jsonArr.length()-1; i++) {
			map = new HashMap<>();
			JSONObject j = jsonArr.getJSONObject(i);
			map.put("name",j.get("name").toString());
			map.put("address",j.get("address").toString());
			map.put("tel",j.get("tel").toString());
			list.add(map);
		}
		System.out.printf("%d 페이지 완료\n",page);
		return list;
	}
}
