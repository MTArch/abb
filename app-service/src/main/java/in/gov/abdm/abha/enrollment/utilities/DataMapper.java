package in.gov.abdm.abha.enrollment.utilities;

import org.springframework.beans.BeanUtils;
/*
suraj singh

 */
public class DataMapper<T> {

	public  T mapper(Object source, Class<T> targetClass) {
		T target = BeanUtils.instantiateClass(targetClass);
		BeanUtils.copyProperties(source, target);
		return target;
	}

}
