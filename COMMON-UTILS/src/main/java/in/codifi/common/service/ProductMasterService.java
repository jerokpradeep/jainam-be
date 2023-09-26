package in.codifi.common.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.springframework.data.domain.Sort;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.entity.primary.ProductMasterEntity;
import in.codifi.common.repository.ProductMasterRepository;
import in.codifi.common.service.spec.ProductMasterServiceSpec;
import in.codifi.common.utility.AppConstants;
import in.codifi.common.utility.StringUtil;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ProductMasterService implements ProductMasterServiceSpec {

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	ProductMasterRepository repository;

	/**
	 * Method to load product master into cache
	 * 
	 * @author DINESH KUMAR
	 * 
	 * Implement and modified by Nesan
	 *
	 * @return
	 */
	@SuppressWarnings("unused")
	@Override
	public Boolean loadProductMaster() {
		try {
			long id = 1;
			Sort sort = Sort.by(Sort.Direction.ASC, "tag");
			List<ProductMasterEntity> productMasterEntitiesChecking = repository.findAll(sort);
			if (!StringUtil.isListNotNullOrEmpty(productMasterEntitiesChecking))
				return false;
			for (ProductMasterEntity entity : productMasterEntitiesChecking) {
				System.out.println(entity.getTag() + entity.getUiValue() + entity.getOdinValue());

				switch (entity.getTag()) {
				case AppConstants.PRODUCT_TYPE:
					if (StringUtil.isNotNullOrEmpty(entity.getOdinValue())) {
						HazelcastConfig.getInstance().getProductTypes().put(entity.getUiValue(), entity.getOdinValue());
						HazelcastConfig.getInstance().getProductTypes().put(entity.getOdinValue(), entity.getUiValue());
						break;
					}
				case AppConstants.ORDER_TYPE:
					if (StringUtil.isNotNullOrEmpty(entity.getOdinValue())) {
						HazelcastConfig.getInstance().getOrderTypes().put(entity.getUiValue(), entity.getOdinValue());
						HazelcastConfig.getInstance().getOrderTypes().put(entity.getOdinValue(), entity.getUiValue());
					}
					break;
				case AppConstants.PRICE_TYPE:
					if (StringUtil.isNotNullOrEmpty(entity.getOdinValue())) {
						HazelcastConfig.getInstance().getPriceTypes().put(entity.getUiValue(), entity.getOdinValue());
						HazelcastConfig.getInstance().getPriceTypes().put(entity.getOdinValue(), entity.getUiValue());
						break;
					}
				default:
					break;
				}

			}
			return true;
		} catch (Exception e) {
			Log.error(e);
		}
		return false;
	}

}
