package cn.com.cis.module.drgsgroup.entity;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import cn.com.cis.utils.DateUtil;

public class AtomicCount {
	
	private AtomicInteger count = new AtomicInteger(0);//记录处理1000条的数量
	private AtomicInteger queryNum = new AtomicInteger(0);//记录查询数量
	private AtomicInteger total = new AtomicInteger(0);//记录处理数量
	private AtomicInteger insertNum = new AtomicInteger(0);//记录达到5000条数据进行批量插入
	private AtomicLong startTime = new AtomicLong(DateUtil.getNowTime());
	private AtomicLong drgsTime = new AtomicLong(DateUtil.getNowTime());
	private AtomicLong globalTime = new AtomicLong(DateUtil.getNowTime());
	
	public int getCount() {
		return count.intValue();
	}
	
	public void setCount(int count) {
		this.count = new AtomicInteger(count);
	}
	
	public int getQueryNum() {
		return queryNum.intValue();
	}
	
	public void setQueryNum(int num) {
		this.queryNum = new AtomicInteger(num);
	}
	
	public void resetQueryNum() {
		this.queryNum = new AtomicInteger(0);
	}
	
	public int getTotal() {
		return total.intValue();
	}
	
	public void setTotal(int total) {
		this.total = new AtomicInteger(total);
	}
	
	public int getInsertNum() {
		return insertNum.intValue();
	}
	
	public void setInsertNum(int insertNum) {
		this.insertNum = new AtomicInteger(insertNum);
	}
	
	public long getStartTime() {
		return startTime.longValue();
	}
	
	public void setStartTime(long start) {
		this.startTime = new AtomicLong(start);
	}
	
	public long getDrgsTime() {
		return drgsTime.longValue();
	}
	
	public void resetDrgsTime(){
		this.drgsTime = new AtomicLong(DateUtil.getNowTime());
	}
	
	public long getGlobalTime() {
		return globalTime.longValue();
	}

	public void resetGlobalTime() {
		this.globalTime = new AtomicLong(DateUtil.getNowTime());
	}

	public int addAndGetNum(int num) {
		return this.queryNum.addAndGet(num);
	}
	
	public int addAndGetTotal(int num) {
		return this.total.addAndGet(num);
	}
	
	public int addAndGetInsertNum(int num) {
		return this.insertNum.addAndGet(num);
	}
	
	public int addAndGetCount(int i) {
		return this.count.addAndGet(1);
	}
	
	public void initStartTime() {
		setStartTime(DateUtil.getNowTime());
	}

	public void initCountAndTotal(){
		setTotal(0);
		setCount(0);
		setInsertNum(0);
	}

	public void resetTime() {
		resetDrgsTime();
		resetGlobalTime();
	}
}
