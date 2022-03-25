package iss.edu.sg.assessment;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import iss.edu.sg.assessment.model.Quotation;
import iss.edu.sg.assessment.service.QuotationService;

@SpringBootTest
class AssessmentApplicationTests {

	@Autowired
	private QuotationService qSvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testGetQuotations() throws Exception {

		List<String> testOrder = new ArrayList<>();
			testOrder.add("durian");
			testOrder.add("plum");
			testOrder.add("pear");

		Optional<Quotation> opt = qSvc.getQuotations(testOrder);

		Assertions.assertTrue(opt.isEmpty());

	}

}
