/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.magnum.mobilecloud.video.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.magnum.mobilecloud.video.client.VideoSvcApi;
import org.magnum.mobilecloud.video.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;


@Controller
public class MySecondController {
	
	@Autowired
	private VideoRepository videoRepository;
	
	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */
	
/*	GET /video
	   - Returns the list of videos that have been added to the
	     server as JSON. The list of videos should be persisted
	     using Spring Data. The list of Video objects should be able 
	     to be unmarshalled by the client into a Collection<Video>.
	   - The return content-type should be application/json, which
	     will be the default if you use @ResponseBody
*/
    @RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.GET)
 	public @ResponseBody Collection<Video> getVideoList() {
 		
    	return Lists.newArrayList(videoRepository.findAll());   	 
 	}
    
/*  POST /video
    - The video metadata is provided as an application/json request
      body. The JSON should generate a valid instance of the 
      Video class when deserialized by Spring's default 
      Jackson library.
    - Returns the JSON representation of the Video object that
      was stored along with any updates to that object made by the server. 
    - **_The server should store the Video in a Spring Data JPA repository.
    	 If done properly, the repository should handle generating ID's._** 
    - A video should not have any likes when it is initially created.
    - You will need to add one or more annotations to the Video object
      in order for it to be persisted with JPA.
*/
    @RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH, method=RequestMethod.POST)
	public @ResponseBody Video addVideo(@RequestBody Video v){
		
    	v.setLikes(0);
    	
		return videoRepository.save(v);
	}
    
 /*   GET /video/{id}
    - Returns the video with the given id or 404 if the video is not found.
*/
    @RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}", method=RequestMethod.GET)
	public @ResponseBody Video getVideoById(@PathVariable("id") long id, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		
		Video video = videoRepository.findOne(id);
		
		if(video==null){
			//response.setStatus(404);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}	
		return video;
	}
    
/*    POST /video/{id}/like
    - Allows a user to like a video. Returns 200 Ok on success, 404 if the
      video is not found, or 400 if the user has already liked the video.
    - The service should should keep track of which users have liked a video and
      prevent a user from liking a video twice. A POJO Video object is provided for 
      you and you will need to annotate and/or add to it in order to make it persistable.
    - A user is only allowed to like a video once. If a user tries to like a video
       a second time, the operation should fail and return 400 Bad Request.
*/
//    @POST(VIDEO_SVC_PATH + "/{id}/like")

    @RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/like", method=RequestMethod.POST)
	public void likeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal p) throws IOException {
		// TODO Auto-generated method stub
    	
		Video video = videoRepository.findOne(id);
		if(video==null){
			//response.setStatus(404);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		String username = p.getName();
		
		boolean isLikeSuccessful = video.likeVideo(username);
		if(isLikeSuccessful){
			videoRepository.save(video);
			//response.setStatus(200);
			response.setStatus(HttpServletResponse.SC_OK);
		}else{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
			
/*			Set<String> likers = video.getLikesUsernames();
			Iterator<String> iterator = likers.iterator();
			
			while(iterator.hasNext()){
				if(iterator.next().equals(username)){
					response.setStatus(400);
				}
			}
			video.like(username);
			video.setLikes(video.getLikes()+1);
			videos.save(video);
			response.setStatus(200);
*/	
	}
    
/*    POST /video/{id}/unlike
    - Allows a user to unlike a video that he/she previously liked. Returns 200 OK
       on success, 404 if the video is not found, and a 400 if the user has not 
       previously liked the specified video.
*/
    @RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/unlike", method=RequestMethod.POST)
    public void unlikeVideo(@PathVariable("id") long id, HttpServletResponse response, Principal p){
    	String username = p.getName();
    	
		Video video = videoRepository.findOne(id);

		if(video == null){
			//response.setStatus(404);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		boolean isUnlikeSuccessful = video.unLikeVideo(username);
		if(isUnlikeSuccessful){
			videoRepository.save(video);
			//response.setStatus(200);
			response.setStatus(HttpServletResponse.SC_OK);
		}else{
			//response.setStatus(400);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
/*		if(video==null){
			response.setStatus(404);
		}else{
			Set<String> likers = video.getLikesUsernames();
			Iterator<String> iterator = likers.iterator();
			
			while(iterator.hasNext()){
				if(iterator.next().equals(username)){
					video.unlike(username);
					video.setLikes(video.getLikes());
					response.setStatus(200);
				}
			}
			response.setStatus(400);
		}
*/
    }
    
/*    GET /video/{id}/likedby
    - Returns a list of the string usernames of the users that have liked the specified
      video. If the video is not found, a 404 error should be generated.
*/
    @RequestMapping(value=VideoSvcApi.VIDEO_SVC_PATH + "/{id}/likedby", method=RequestMethod.POST)
    public @ResponseBody List<String> getUsersWhoLikedVideo(@PathVariable("id") long id, HttpServletResponse response){
    	
		Video video = videoRepository.findOne(id);
		if(video==null){
			//response.setStatus(404);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		return video.getUsersWhoLikedTheVideo();
    }
    
/*    GET /video/search/findByName?title={title}
    - Returns a list of videos whose titles match the given parameter or an empty
      list if none are found.
      @GET(VIDEO_TITLE_SEARCH_PATH)
	public Collection<Video> findByTitle(@Query(TITLE_PARAMETER) String title);
*/    
    @RequestMapping(value=VideoSvcApi.VIDEO_TITLE_SEARCH_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Video> findByTitle(@RequestParam(VideoSvcApi.VIDEO_TITLE_SEARCH_PATH) String title){
    	
    	return videoRepository.findByName(title);	
    }
    
    
/*    GET /video/search/findByDurationLessThan?duration={duration}
    - Returns a list of videos whose durations are less than the given parameter or
      an empty list if none are found.
      @GET(VIDEO_DURATION_SEARCH_PATH)
	public Collection<Video> findByDurationLessThan(@Query(DURATION_PARAMETER) long duration);
*/
    @RequestMapping(value=VideoSvcApi.VIDEO_DURATION_SEARCH_PATH, method=RequestMethod.GET)
    public @ResponseBody Collection<Video> findByDuration(@RequestParam(VideoSvcApi.VIDEO_DURATION_SEARCH_PATH) Long duration){
    	
    	return videoRepository.findByDurationLessThan(duration);	
    }
    
	@RequestMapping(value="/go",method=RequestMethod.GET)
	public @ResponseBody String goodLuck(){
		return "Good Luck!";
	}
	
}
