package com.shinemo.publish.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang.ArrayUtils;


public class Flag implements Serializable{

	private static final long serialVersionUID = -4332670447885213927L;

	public final static long FLAG_MASK_ALL_OFF = 0;
	public final static long FLAG_MASK_ALL_ON = Long.MAX_VALUE;
	public final static long FLAG_MASK_1 = (long) Math.pow(2, 0);
	public final static long FLAG_MASK_2 = (long) Math.pow(2, 1);
	public final static long FLAG_MASK_3 = (long) Math.pow(2, 2);
	public final static long FLAG_MASK_4 = (long) Math.pow(2, 3);
	public final static long FLAG_MASK_5 = (long) Math.pow(2, 4);
	public final static long FLAG_MASK_6 = (long) Math.pow(2, 5);
	public final static long FLAG_MASK_7 = (long) Math.pow(2, 6);
	public final static long FLAG_MASK_8 = (long) Math.pow(2, 7);
	public final static long FLAG_MASK_9 = (long) Math.pow(2, 8);
	public final static long FLAG_MASK_10 = (long) Math.pow(2, 9);
	public final static long FLAG_MASK_11 = (long) Math.pow(2, 10);
	public final static long FLAG_MASK_12 = (long) Math.pow(2, 11);
	public final static long FLAG_MASK_13 = (long) Math.pow(2, 12);
	public final static long FLAG_MASK_14 = (long) Math.pow(2, 13);
	public final static long FLAG_MASK_15 = (long) Math.pow(2, 14);
	public final static long FLAG_MASK_16 = (long) Math.pow(2, 15);
	public final static long FLAG_MASK_17 = (long) Math.pow(2, 16);
	public final static long FLAG_MASK_18 = (long) Math.pow(2, 17);
	public final static long FLAG_MASK_19 = (long) Math.pow(2, 18);
	public final static long FLAG_MASK_20 = (long) Math.pow(2, 19);
	public final static long FLAG_MASK_21 = (long) Math.pow(2, 20);
	public final static long FLAG_MASK_22 = (long) Math.pow(2, 21);
	public final static long FLAG_MASK_23 = (long) Math.pow(2, 22);
	public final static long FLAG_MASK_24 = (long) Math.pow(2, 23);
	public final static long FLAG_MASK_25 = (long) Math.pow(2, 24);
	public final static long FLAG_MASK_26 = (long) Math.pow(2, 25);
	public final static long FLAG_MASK_27 = (long) Math.pow(2, 26);
	public final static long FLAG_MASK_28 = (long) Math.pow(2, 27);
	public final static long FLAG_MASK_29 = (long) Math.pow(2, 28);
	public final static long FLAG_MASK_30 = (long) Math.pow(2, 29);
	public final static long FLAG_MASK_31 = (long) Math.pow(2, 30);
	public final static long FLAG_MASK_32 = (long) Math.pow(2, 31);
	public final static long FLAG_MASK_33 = (long) Math.pow(2, 32);
	public final static long FLAG_MASK_34 = (long) Math.pow(2, 33);
	public final static long FLAG_MASK_35 = (long) Math.pow(2, 34);
	public final static long FLAG_MASK_36 = (long) Math.pow(2, 35);
	public final static long FLAG_MASK_37 = (long) Math.pow(2, 36);
	public final static long FLAG_MASK_38 = (long) Math.pow(2, 37);
	public final static long FLAG_MASK_39 = (long) Math.pow(2, 38);
	public final static long FLAG_MASK_40 = (long) Math.pow(2, 39);
	public final static long FLAG_MASK_41 = (long) Math.pow(2, 40);
	public final static long FLAG_MASK_42 = (long) Math.pow(2, 41);
	public final static long FLAG_MASK_43 = (long) Math.pow(2, 42);
	public final static long FLAG_MASK_44 = (long) Math.pow(2, 43);
	public final static long FLAG_MASK_45 = (long) Math.pow(2, 44);
	public final static long FLAG_MASK_46 = (long) Math.pow(2, 45);
	public final static long FLAG_MASK_47 = (long) Math.pow(2, 46);
	public final static long FLAG_MASK_48 = (long) Math.pow(2, 47);
	public final static long FLAG_MASK_49 = (long) Math.pow(2, 48);
	public final static long FLAG_MASK_50 = (long) Math.pow(2, 49);
	public final static long FLAG_MASK_51 = (long) Math.pow(2, 50);
	public final static long FLAG_MASK_52 = (long) Math.pow(2, 51);
	public final static long FLAG_MASK_53 = (long) Math.pow(2, 52);
	public final static long FLAG_MASK_54 = (long) Math.pow(2, 53);
	public final static long FLAG_MASK_55 = (long) Math.pow(2, 54);
	public final static long FLAG_MASK_56 = (long) Math.pow(2, 55);
	public final static long FLAG_MASK_57 = (long) Math.pow(2, 56);
	public final static long FLAG_MASK_58 = (long) Math.pow(2, 57);
	public final static long FLAG_MASK_59 = (long) Math.pow(2, 58);
	public final static long FLAG_MASK_60 = (long) Math.pow(2, 59);
	public final static long FLAG_MASK_61 = (long) Math.pow(2, 60);
	public final static long FLAG_MASK_62 = (long) Math.pow(2, 61);
	public final static long FLAG_MASK_63 = (long) Math.pow(2, 62);

	/**
	 * 医院标
	 *
	 */
	public enum HospitalFlag {
		COMPANION(1,FLAG_MASK_1,"预约陪诊", "随时随地 方便快捷 爱心陪诊", ColorEnum.LIMEGREEN), // 是否有预约&陪诊服务
		FAMOUS_DOCTOR(2,FLAG_MASK_2,"名医加号", "三甲名医 号源充足\n 即约即有 专业陪护", ColorEnum.GOLD);// 是否有名医加号服务

		private HospitalFlag(int index,long mask, String name, String desc, ColorEnum colorEnum) {
			this.mask = mask;
			this.name = name;
			this.desc = desc;
			this.index = index;
			this.colorEnum = colorEnum;
		}

		private final long mask;
		private final String name;
		private final String desc;
		private final int index;
		private final ColorEnum colorEnum;

		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}

		public String getName() {
			return name;
		}

		public int getIndex() {
			return index;
		}

		public ColorEnum getColorEnum() {
			return colorEnum;
		}

		public static HospitalFlag getByIndex(final int index) {
			for (HospitalFlag type : HospitalFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return COMPANION;
		}

		public static JSONArray flagString(long mask) {
			Flag.HospitalFlag[] vals = Flag.HospitalFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.HospitalFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("name", f.getName());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.HospitalFlag[] vals = Flag.HospitalFlag.values();
			for (Flag.HospitalFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}

	}

	/**
	 * 医院等级标,互斥标
	 *
	 */
	public enum HospitalGradeFlag {

		THREE_SPECIAL(1,FLAG_MASK_32|FLAG_MASK_31,"三级特等"), // 三级特等
		THREE_A(2,FLAG_MASK_32|FLAG_MASK_30,"三级甲等"), // 三级甲等
		THREE_B(3,FLAG_MASK_32|FLAG_MASK_29,"三级乙等"), // 三级乙等
		THREE_C(4,FLAG_MASK_32|FLAG_MASK_28,"三级丙等"), // 三级丙等
		THREE(5,FLAG_MASK_32|FLAG_MASK_25,"三级"), // 三级


		TWO_A(6,FLAG_MASK_24|FLAG_MASK_23,"二级甲等"), // 二级甲等
		TWO_B(7,FLAG_MASK_24|FLAG_MASK_22,"二级乙等"), // 二级乙等
		TWO_C(8,FLAG_MASK_24|FLAG_MASK_21,"二级丙等"), // 二级丙等
		TWO(9,FLAG_MASK_24|FLAG_MASK_17,"二级"), // 二级


		ONE_A(10,FLAG_MASK_16|FLAG_MASK_15,"一级甲等"), // 一级甲等
		ONE_B(11,FLAG_MASK_16|FLAG_MASK_14,"一级乙等"), // 一级乙等
		ONE_C(12,FLAG_MASK_16|FLAG_MASK_13,"一级丙等"), // 一级丙等
		ONE(13,FLAG_MASK_16|FLAG_MASK_9,"一级"), // 一级


		ZERO(14,FLAG_MASK_8|FLAG_MASK_1,"未知等级"), // 未知等级  10000001

		THREE_ALL(15,FLAG_MASK_32,"三级所有，范围查询使用"), // 三级所有
		TWO_ALL(16,FLAG_MASK_24,"二级所有，范围查询使用"), // 二级所有
		ONE_ALL(17,FLAG_MASK_16,"一级所有，范围查询使用"), // 一级所有
		ZERO_ALL(18,FLAG_MASK_8,"未知定级所有，范围查询使用"); // 未知定级  10000001

		private HospitalGradeFlag(int index,long mask,String desc) {
			this.mask = mask;
			this.desc = desc;
			this.index = index;
		}

		private final int index;
		private final long mask;
		private final String desc;

		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}

		public int getIndex() {
			return index;
		}

		public static HospitalGradeFlag getByIndex(final int index) {
			for (HospitalGradeFlag type : HospitalGradeFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return ZERO;
		}

		public static HospitalGradeFlag getByMask(final long mask) {
			for (HospitalGradeFlag type : HospitalGradeFlag.values()) {
				if (type.mask == mask) {
					return type;
				}
			}
			return ZERO;
		}

		public static JSONArray flagString(long flag) {
			Flag.HospitalGradeFlag[] vals = Flag.HospitalGradeFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.HospitalGradeFlag f : vals) {
				if(FlagHelper.hasFlag(flag, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("desc", f.getDesc());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.HospitalGradeFlag[] vals = Flag.HospitalGradeFlag.values();
			for (Flag.HospitalGradeFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}
	}

	public enum ReferralDeptFlag {
		SPECIAL_DEPT(1,FLAG_MASK_1,"特色科室"),// 是否有特色科室
		COMPANION(2,FLAG_MASK_2,"陪诊");// 是否有陪诊服务

		private ReferralDeptFlag(int index,long mask, String desc) {
			this.mask = mask;
			this.desc = desc;
			this.index = index;
		}

		private final int index;
		private final long mask;
		private final String desc;

		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}

		public int getIndex() {
			return index;
		}

		public static ReferralDeptFlag getByIndex(final int index) {
			for (ReferralDeptFlag type : ReferralDeptFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return SPECIAL_DEPT;
		}

		public static JSONArray flagString(long flag) {
			Flag.ReferralDeptFlag[] vals = Flag.ReferralDeptFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.ReferralDeptFlag f : vals) {
				if(FlagHelper.hasFlag(flag, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("desc", f.getDesc());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.ReferralDeptFlag[] vals = Flag.ReferralDeptFlag.values();
			for (Flag.ReferralDeptFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}
	}

	public enum ReferralDeptmentFlag {
		ZERO(0,FLAG_MASK_ALL_ON,"无标科室"),// 无标科室
		NORMAL(1,FLAG_MASK_1,"普通科室"),// 普通科室
		EXPERT(2,FLAG_MASK_2,"专家科室");// 专家科室

		private ReferralDeptmentFlag(int index,long mask, String desc) {
			this.mask = mask;
			this.desc = desc;
			this.index = index;
		}

		private final int index;
		private final long mask;
		private final String desc;

		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}

		public int getIndex() {
			return index;
		}

		public static ReferralDeptmentFlag getByIndex(final int index) {
			for (ReferralDeptmentFlag type : ReferralDeptmentFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return NORMAL;
		}

		public static JSONArray flagString(long flag) {
			Flag.ReferralDeptmentFlag[] vals = Flag.ReferralDeptmentFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.ReferralDeptmentFlag f : vals) {
				if(FlagHelper.hasFlag(flag, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("desc", f.getDesc());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.ReferralDeptmentFlag[] vals = Flag.ReferralDeptmentFlag.values();
			for (Flag.ReferralDeptmentFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}
	}

	public enum ReferralDoctorFlag {
		FAMOUS_DOCTOR(1,FLAG_MASK_1,"知名专家"), // 是否知名专家
		BOOKABLE(2,FLAG_MASK_2,"可预约");// 是否可预约

		private ReferralDoctorFlag(int index,long mask, String desc) {
			this.mask = mask;
			this.desc = desc;
			this.index = index;
		}

		private final long mask;
		private final String desc;
		private final int index;
		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}

		public int getIndex() {
			return index;
		}

		public static ReferralDoctorFlag getByIndex(final int index) {
			for (ReferralDoctorFlag type : ReferralDoctorFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return FAMOUS_DOCTOR;
		}

		public static JSONArray flagString(long flag) {
			Flag.ReferralDoctorFlag[] vals = Flag.ReferralDoctorFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.ReferralDoctorFlag f : vals) {
				if(FlagHelper.hasFlag(flag, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("desc", f.getDesc());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.ReferralDoctorFlag[] vals = Flag.ReferralDoctorFlag.values();
			for (Flag.ReferralDoctorFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}
	}

	public enum ReferralTinspecFlag {
		COMPANION(1,FLAG_MASK_1,"陪诊");// 是否有陪诊服务

		private ReferralTinspecFlag(int index,long mask, String desc) {
			this.mask = mask;
			this.desc = desc;
			this.index = index;
		}

		private final long mask;
		private final String desc;
		private final int index;

		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}
		public int getIndex() {
			return index;
		}

		public static ReferralTinspecFlag getByIndex(final int index) {
			for (ReferralTinspecFlag type : ReferralTinspecFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return COMPANION;
		}

		public static JSONArray flagString(long flag) {
			Flag.ReferralTinspecFlag[] vals = Flag.ReferralTinspecFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.ReferralTinspecFlag f : vals) {
				if(FlagHelper.hasFlag(flag, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("desc", f.getDesc());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.ReferralTinspecFlag[] vals = Flag.ReferralTinspecFlag.values();
			for (Flag.ReferralTinspecFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}

	}

	public enum MgDeptFlag {
		ZERO(0,FLAG_MASK_ALL_ON,"未打标"),// 未打标
		FAMOUS_DOCTOR_DEPT(1,FLAG_MASK_1,"名医馆科室"),//是否名医馆科室  以后废弃掉这个数据
		IS_ENABLE(2,FLAG_MASK_2,"启用");//

		private MgDeptFlag(int index,long mask, String desc) {
			this.mask = mask;
			this.desc = desc;
			this.index = index;
		}

		private final long mask;
		private final String desc;
		private final int index;

		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}

		public int getIndex() {
			return index;
		}

		public static MgDeptFlag getByIndex(final int index) {
			for (MgDeptFlag type : MgDeptFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return FAMOUS_DOCTOR_DEPT;
		}

		public static JSONArray flagString(long flag) {
			Flag.MgDeptFlag[] vals = Flag.MgDeptFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.MgDeptFlag f : vals) {
				if(FlagHelper.hasFlag(flag, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("desc", f.getDesc());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.MgDeptFlag[] vals = Flag.MgDeptFlag.values();
			for (Flag.MgDeptFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}

	}
	//社区医生标
	public enum DoctorFlag {
		TEST(1,FLAG_MASK_1,"测试账号"),MPUSH(1,FLAG_MASK_2,"使用mpush");//

		private DoctorFlag(int index,long mask, String desc) {
			this.mask = mask;
			this.desc = desc;
			this.index = index;
		}

		private final long mask;
		private final String desc;
		private final int index;
		public long getMask() {
			return mask;
		}

		public String getDesc() {
			return desc;
		}
		
		public int getIndex() {
			return index;
		}

		public static DoctorFlag getByIndex(final int index) {
			for (DoctorFlag type : DoctorFlag.values()) {
				if (type.index == index) {
					return type;
				}
			}
			return null;
		}

		public static JSONArray flagString(long flag) {
			Flag.DoctorFlag[] vals = Flag.DoctorFlag.values();
			List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
			for (Flag.DoctorFlag f : vals) {
				if(FlagHelper.hasFlag(flag, f.getMask())) {
					Map<String, Object> map  = new HashMap<String, Object>();
					map.put("index", f.getIndex());
					map.put("desc", f.getDesc());
					mapList.add(map);
				}
			}
			return JSONArray.fromObject(mapList);
		}

		public static int[] flagIndexs(long mask) {
			List<Integer> indexList = new ArrayList<Integer>();
			Flag.DoctorFlag[] vals = Flag.DoctorFlag.values();
			for (Flag.DoctorFlag f : vals) {
				if(FlagHelper.hasFlag(mask, f.getMask())) {
					indexList.add(f.getIndex());
				}
			}
			return ArrayUtils.toPrimitive(indexList.toArray(new Integer[indexList.size()]));
		}
	}

	public static void main(String[] args) {
		//add hospital  名医加号tag
		long hospital_flag = 0;

//		help.add(HospitalFlag.FAMOUS_DOCTOR.getMask());

//		hospital_flag = hospital_flag & ~help.getMask()|help.getValue();
//
//		System.out.println(hospital_flag);


//		//add hospital 预约&陪诊服务 tag
//		FlagHelper help2 = FlagHelper.build();
//		help2.add(HospitalFlag.COMPANION.getMask());
//		hospital_flag = hospital_flag & ~help2.getMask()|help2.getValue();
//		System.out.println(hospital_flag);
//
//		//remove 名医加号tag
//		FlagHelper help3 = FlagHelper.build();
//		help3.remove(HospitalFlag.FAMOUS_DOCTOR.getMask());
//		hospital_flag = hospital_flag & ~help3.getMask()|help3.getValue();
//		System.out.println(hospital_flag);
//
//		//remove 预约&陪诊服务 tag
//		FlagHelper help4 = FlagHelper.build();
//		help4.remove(HospitalFlag.COMPANION.getMask());
//		hospital_flag = hospital_flag & ~help4.getMask()|help4.getValue();
//		System.out.println(hospital_flag);
//
//		System.out.println(HospitalGradeFlag.ZERO.mask);
	}


}
