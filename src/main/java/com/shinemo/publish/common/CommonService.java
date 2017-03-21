package com.shinemo.publish.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class CommonService<Q,D> implements BaseService<Q, D> {
	
	private static Logger log = LoggerFactory.getLogger(CommonService.class);
	
	public abstract Mapper<Q, D> getMapper();
	
	public Result<List<D>> find(Q query) {
		ResultBuilder<List<D>> builder = new ResultBuilder<List<D>>();
		try{
			//step1 
			fixQuery(query);
			
			Long count = getMapper().count(query);
			if(count!=null&&count>0){
				List<D> ret = getMapper().find(query);
				builder.setValue(ret);
			}else{
				builder.setValue(new ArrayList<D>(0));
			}
			if(count!=null){
				builder.setRemark(count.toString());
			}else{
				builder.setRemark("0");
			}
			
			builder.setSuccess(true);
		}catch (Exception e) {
			log.error("find:"+query.toString(),e);
			builder.setSuccess(false).setError(Errors.ERROR_500).setThrowable(e);
		}
		return builder.build();
	}
	
	public Result<D> get(Q query) {
		ResultBuilder<D> builder = new ResultBuilder<D>();
		try{
			D ret = getMapper().get(query);
			builder.setValue(ret);
			builder.setSuccess(true);
		}catch (Exception e) {
			log.error("get:"+query.toString(),e);
			builder.setSuccess(false).setError(Errors.ERROR_500).setThrowable(e);
		}
		return builder.build();
	}

	public Result<Long> add(D temp) {
		ResultBuilder<Long> builder = new ResultBuilder<Long>();
		try{
			if(checkAddParam(temp)){
				getMapper().insert(temp);
				builder.setValue(getId(temp));
			}else{
				builder.setError(Errors.ERROR_500);
			}
			builder.setSuccess(true);
		}catch (Exception e) {
			log.error("add:"+temp.toString(),e);
			builder.setSuccess(false).setError(Errors.ERROR_500).setThrowable(e);
		}
		return builder.build();
	}

	public Result<Boolean> update(D temp) {
		ResultBuilder<Boolean> builder = new ResultBuilder<Boolean>();
		try{
			if(checkUpdateParam(temp)){
				int updateRet = getMapper().update(temp);
				
				builder.setValue(updateRet>1?true:false);
			}else{
				builder.setError(Errors.ERROR_500);
			}
			builder.setSuccess(true);
		}catch (Exception e) {
			log.error("update:"+temp,e);
			builder.setSuccess(false).setError(Errors.ERROR_500).setThrowable(e);
		}
		return builder.build();
	}

	public Result<Boolean> delete(Q query) {
		ResultBuilder<Boolean> builder = new ResultBuilder<Boolean>();
		try{
			if(checkDeleteParam(query)){
				int updateRet = getMapper().delete(query);
				builder.setValue(updateRet>1?true:false);
			}else{
				builder.setError(Errors.ERROR_500);
			}
			builder.setSuccess(true);
		}catch (Exception e) {
			log.error("delete:"+query,e);
			builder.setSuccess(false).setError(Errors.ERROR_500).setThrowable(e);
		}
		return builder.build();
	}
	
	public Result<Long> count(Q query) {
		ResultBuilder<Long> builder = new ResultBuilder<Long>();
		try{
			Long ret = getMapper().count(query);
			builder.setValue(ret);
			builder.setSuccess(true);
		}catch (Exception e) {
			log.error("get:"+query.toString(),e);
			builder.setSuccess(false).setError(Errors.ERROR_500).setThrowable(e);
		}
		return builder.build();
	}
	
	public void fixQuery(Q query){
		//空实现
	}
	
	public boolean checkAddParam(D temp){
		return true;
	}
	
	public boolean checkDeleteParam(Q query){
		return true;
	}
	
	public boolean checkUpdateParam(D temp){
		return true;
	}
	
	public abstract Long getId(D temp);

}