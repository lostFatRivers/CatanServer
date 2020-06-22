package com.jokerbee.cache;

import com.jokerbee.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.util.Pool;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class RedisClient {
	private static Logger logger = LoggerFactory.getLogger(RedisClient.class);

	/** 获取redis锁成功 */
	public static final String LOCK_SUCCESS = "OK";
	/** 如果可以存在则不做任何操作, 如果不存在则设置 */
	public static final String SET_IF_NOT_EXIST = "NX";
	/** 锁有效时间, 超时则自动删除 */
	public static final String SET_WITH_EXPIRE_TIME = "PX";

	/** 释放锁的redis脚本 */
	public static final String UNLOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
	/** 释放锁成功的返回值 */
	public static final Long UNLOCK_SUCCESS = 1L;

	private Pool<Jedis> jedisPool;

	/** 是否是集群模式 */
	private boolean cluster;
	/** 当为集群模式时, 为ip列表, 用','隔开 */
	private String ip;
	private int port;
	/** 集群名 */
	private String masterName;
	private String pwd;
	/** 可用连接实例的最大数目，默认值为8 */
	private int maxActive;
	/** 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException */
	private long maxWait;
	/** socket读取输入InputStream的超时时间 */
	private int timeout;

	public RedisClient(boolean cluster, String ip, int port, String masterName, String pwd, int maxActive, long maxWait, int timeout) {
		this.cluster = cluster;
		this.ip = ip;
		this.port = port;
		this.masterName = masterName;
		this.pwd = pwd;
		this.maxActive = maxActive;
		this.maxWait = maxWait;
		this.timeout = timeout;
	}

	public void startup() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(maxActive);
		poolConfig.setMaxWaitMillis(maxWait);
		if (cluster) {
			Set<String> sentinels = new HashSet<>();
			String[] split = ip.split(",");
			for (String eachHost : split) {
				if (StringUtils.isEmpty(eachHost)) {
					continue;
				}
				sentinels.add(eachHost + ":" + port);
			}
			jedisPool = new JedisSentinelPool(masterName, sentinels, poolConfig, timeout, pwd);
		} else {
			jedisPool = new JedisPool(poolConfig, ip, port, timeout, pwd);
			logger.info("redis client start, ip:{}, port:{}", ip, port);
		}
		try {
			if (ping()) {
				logger.info("redis client start ok...");
			}
		} catch (Exception e) {
			logger.info("redis client start failed...", e);
		}

	}

	public void shutdown() {
		if (jedisPool != null) {
			jedisPool.destroy();
		}
	}

	public boolean ping() {
		boolean result;
		try (Jedis jedis = jedisPool.getResource()) {
			result = jedis.ping().equals("PONG");
		}
		return result;
	}

	public byte[] get(String key) {
		byte[] result;
		try (Jedis jedis = jedisPool.getResource()) {
			result = jedis.get(key.getBytes(StandardCharsets.UTF_8));
		}
		return result;
	}

	public void set(String key, byte[] value) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.set(key.getBytes(StandardCharsets.UTF_8), value);
		}
	}

	public void setString(String key, String value) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.set(key, value);
		}
	}

	/**
	 * string
	 */
	public String getString(String key) {
		String result;
		try (Jedis jedis = jedisPool.getResource()) {
			result = jedis.get(key);
		}
		return result;
	}

	/**
	 * string
	 */
	public void del(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.del(key);
		}
	}
	
	public boolean exists(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.exists(key);
		}
	}

	public void rename(String oldKey, String newKey) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.rename(oldKey, newKey);
		}
	}

	/**
	 * list 在名称为key的list尾添加一个值为value的元素
	 */
	public void rpush(String key, String value) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.rpush(key, value);
		}
	}

	/**
	 * list 在名称为key的list头添加一个值为value的 元素
	 */
	public void lpush(String key, String value) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.lpush(key, value);
		}
	}

	/**
	 * 有多少数据需要保存
	 */
	public long getListLen(String key) {
		long len;
		try (Jedis jedis = jedisPool.getResource()) {
			len = jedis.llen(key);
		}
		return len;
	}

	/**
	 * list 返回并删除名称为key的list中的首元素
	 */
	public String lpop(String key) {
		String v;
		try (Jedis jedis = jedisPool.getResource()) {
			v = jedis.lpop(key);
		}
		return v;
	}

	/**
	 * list 返回并删除名称为key的list中的尾元素
	 */
	public String rpop(String key) {
		String v;
		try (Jedis jedis = jedisPool.getResource()) {
			v = jedis.rpop(key);
		}
		return v;
	}

	/**
	 * 获取list
	 */
	public List<String> getlist(String key) {
		List<String> list;
		try (Jedis jedis = jedisPool.getResource()) {
			list = jedis.lrange(key, 0, -1);
		}
		return list;
	}

	/**
	 * 删除指定元素
	 */
	public void lrem(String key, String v) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.lrem(key, 1, v);
		}
	}

	/**
	 * set add
	 */
	public void sadd(String key, String value) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.sadd(key, value);
		}
	}

	/**
	 * set get all
	 */
	public Set<String> smembers(String key) {
		Set<String> v;
		try (Jedis jedis = jedisPool.getResource()) {
			v = jedis.smembers(key);
		}
		return v;
	}

	/**
	 * is contain
	 */
	public boolean sismember(String key, String member) {
		boolean v = true;
		try (Jedis jedis = jedisPool.getResource()) {
			v = jedis.sismember(key, member);
		}
		return v;
	}

	/**
	 * set remove
	 */
	public void srem(String key, String v) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.srem(key, v);
		}
	}

	/**
	 * redis zadd
	 */
	public void zadd(String key, double score, String member) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.zadd(key, score, member);
		}
	}

	/**
	 * zincrby
	 */
	public void zincrby(String key, double score, String member) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.zincrby(key, score, member);
		}
	}

	/**
	 * redis delele(谨慎操作)
	 */
	public void redisDelete(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.del(key);
		}
	}

	/**
	 * 返回当前名次
	 */
	public Long zrevrank(String key, String member) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrevrank(key, member);
		}
	}

	/**
	 * 返回当前名次
	 */
	public Long zrank(String key, String member) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrank(key, member);
		}
	}

	/**
	 * the all the elements in the sorted set at key between min and max
	 */
	public Set<String> zrange(String key, long start, long end) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrange(key, start, end);
		}
	}

	/**
	 * the all the elements in the sorted set at key between min and max
	 */
	public Set<Tuple> zrangeWithScore(String key, long start, long end) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrangeWithScores(key, start, end);
		}
	}
	
	/**
	 * 地图增加;
	 *
	 * @param longitude 经度
	 * @param latitude 纬度
	 * @param value 地名
	 */
	public long geoadd(String key, double longitude, double latitude, String value) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.geoadd(key, longitude, latitude, value);
		}
	}
	
	/**
	 * 附近点, 从小到大排序;
	 *
	 * @param value 地名
	 * @param radius 距离,单位 km
	 */
	public List<String> georadiusByMemberAsc(String key, String value, double radius) {
		try (Jedis jedis = jedisPool.getResource()) {
			List<GeoRadiusResponse> list = jedis.georadiusByMember(key, value, radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().sortAscending().count(5));
			List<String> values = new ArrayList<>();
			for (GeoRadiusResponse eachResp : list) {
				values.add(eachResp.getMemberByString());
			}
			return values;
		}
	}

	/**
	 * @return Integer reply, specifically: 1 if the new element was removed 0
	 *         if the new element was not a member of the set
	 */
	public long zrem(String key, final String... members) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrem(key, members);
		}
	}

	/**
	 * the all the elements in the sorted set at key between min and max
	 */
	public String zrangeOne(String key, long index) {
		String result = "";
		try (Jedis jedis = jedisPool.getResource()) {
			Set<String> set = jedis.zrange(key, index, index);
			if (set.size() == 1) {
				for (String item : set) {
					result = item;
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * the all the elements in the sorted set at key between min and max
	 */
	public Set<String> zrevrange(String key, long start, long end) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrevrange(key, start, end);
		}
	}

	/**
	 * the all the elements in the sorted set at key between min and max;
	 */
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zrevrangeWithScores(key, start, end);
		}
	}

	/**
	 * @return the set cardinality (number of elements).
	 */
	public long scard(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.scard(key);
		}
	}

	/**
	 * @return the sorted set cardinality (number of elements).
	 */
	public long zcard(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.zcard(key);
		}
	}

	/**
	 * @return the sorted set
	 */
	public double zscore(String key, String member) {
		try (Jedis jedis = jedisPool.getResource()) {
			Double zscore = jedis.zscore(key, member);
			return zscore == null ? 0d : zscore;
		}
	}

	/**
	 * Remove all elements in the sorted set at key with rank between start and
	 * end;
	 */
	public void zremrangeByRank(String key, long start, long end) {
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.zremrangeByRank(key, start, end);
		}
	}

	/**
	 * Remove all elements in the sorted set at key;
	 */
	public void zremrangeAll(String key) {
		zremrangeByRank(key, 0, -1);
	}

	/**
	 * Set the specified hash field to the specified value. <br/>
	 * If key does not exist, a new key holding a hash is created. <br/>
	 * Time complexity: O(1)
	 */
	public void hset(String key, String field, String value) {
		if (StringUtils.isEmpty(field)) {
			return;
		}
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.hset(key, field, value);
		}
	}

	/**
	 * If key holds a hash, retrieve the value associated to the specified
	 * field. <br/>
	 * If the field is not found or the key does not exist, a special 'nil'
	 * value is returned.
	 */
	public String hget(String key, String field) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hget(key, field);
		}
	}

	/**
	 * Test for existence of a specified field in a hash. Time complexity: O(1)
	 */
	public boolean hexists(String key, String field) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hexists(key, field);
		}
	}

	/**
	 * Return the number of items in a hash.
	 */
	public long hlen(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hlen(key);
		}
	}

	/**
	 * Remove the specified field from an hash stored at key.
	 */
	public long hdel(String key, String field) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.hdel(key, field);
		}
	}

	/**
	 * Increment the number stored at key by one. If the key does not exist or
	 * contains a value of a wrong type, set the key to the value of "0" before
	 * to perform the increment operation.
	 */
	public long incr(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.incr(key);
		}
	}

	/**
	 * 获取redis分布式锁, <br>
	 * 需要提前生成请求id, 确保释放锁时不会出现误释放, 超时时间: 3s;
	 * 
	 * @param key
	 *            redis key;
	 * @param requireId
	 *            本次加锁请求的唯一id;
	 */
	public boolean lock(String key, String requireId) {
		try (Jedis jedis = jedisPool.getResource()) {
			String result = jedis.set(key, requireId, SetParams.setParams().nx().px(3 * TimeUtil.SECOND_MILLIS));
			return LOCK_SUCCESS.equals(result);
		} catch (Exception e) {
			logger.error("redis lock get failed, key:{}, requireId:{}", key, requireId, e);
			return false;
		}
	}

	/**
	 * 释放redis分布式锁, <br>
	 * 需要请求id, 若请求id与当前锁的id不同, 则会释放失败;
	 * 
	 * @param key
	 *            redis key;
	 * @param requireId
	 *            加锁时的唯一id;
	 * @return 是否释放锁成功;
	 */
	public boolean unlock(String key, String requireId) {
		try (Jedis jedis = jedisPool.getResource()) {
			Object result = jedis.eval(UNLOCK_SCRIPT, Collections.singletonList(key),
					Collections.singletonList(requireId));
			if (!UNLOCK_SUCCESS.equals(result)) {
				logger.error("redis lock release failed, key:{}, result:{}", key, result);
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("redis lock release failed, key:{}", key, e);
			return false;
		}
	}

}
