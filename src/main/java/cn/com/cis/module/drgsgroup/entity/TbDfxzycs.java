package cn.com.cis.module.drgsgroup.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//TB_DFXCS	多发性重要创伤表，用于最后匹配排序
public class TbDfxzycs extends RedisBean{
	
	private static final long serialVersionUID = -7768555724994571001L;

	private String icd10;//诊断编码
	
	private String bwCode;//

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bwCode == null) ? 0 : bwCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TbDfxzycs other = (TbDfxzycs) obj;
		if (bwCode == null) {
			if (other.bwCode != null)
				return false;
		} else if (!bwCode.equals(other.bwCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TbDfxzycs [icd10=" + icd10 + ", bwCode=" + bwCode + "]";
	}
	
}
