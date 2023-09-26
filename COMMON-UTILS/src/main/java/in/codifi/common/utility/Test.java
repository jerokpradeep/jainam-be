package in.codifi.common.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class Test {

	public static void main(String[] args) {

//		LocalTime time = LocalTime.of(00, 00, 01); // Replace with your desired time
//		System.out.println("time--"+time);
//		long milliseconds = time.truncatedTo(ChronoUnit.SECONDS).toNanoOfDay() / 1_000_000;
//		System.out.println("milliseconds--"+milliseconds);
//		
//		LocalTime timems = LocalTime.of(9, 15, 00); // Replace with your desired time
//		System.out.println("timems--"+timems);
//		long millisecondsms = timems.truncatedTo(ChronoUnit.SECONDS).toNanoOfDay() / 1_000_000;
//		System.out.println("millisecondsms--"+millisecondsms);
//		
//		LocalTime timeEs = LocalTime.of(15, 30, 00); // Replace with your desired time
//		System.out.println("timeEs--"+timeEs);
//		long millisecondsEs = timeEs.truncatedTo(ChronoUnit.SECONDS).toNanoOfDay() / 1_000_000;
//		System.out.println("millisecondsEs--"+millisecondsEs);
//		
//		LocalTime timeEs1 = LocalTime.of(23, 59, 59); // Replace with your desired time
//		System.out.println("timeEs--"+timeEs1);
//		long millisecondsEs1 = timeEs1.truncatedTo(ChronoUnit.SECONDS).toNanoOfDay() / 1_000_000;
//		System.out.println("millisecondsEs--"+millisecondsEs1);
//		
//		String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
//		System.out.println("timeStamp--"+timeStamp);
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//        LocalTime localTime = LocalTime.parse(timeStamp, formatter);
//		long millisecondss = localTime.truncatedTo(ChronoUnit.SECONDS).toNanoOfDay() / 1_000_000;
//		System.out.println("millisecondss--"+millisecondss);

//		Date date = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-yy");
//		String strDate = formatter.format(currentDate);
//		
//        DateTimeFormatter formattesssr = DateTimeFormatter.ofPattern("yyyy-MM-yy");
//        LocalDate datesss = LocalDate.parse(strDate, formattesssr);
		
		Date currentDate = new Date();
		System.out.println("currentDate--" + currentDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, 1);
		Date futureDate = calendar.getTime();
		System.out.println("futureDate--" + futureDate);

		SimpleDateFormat formatters = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'z'");
		Date fromDate = currentDate;
		Date toDate = futureDate;
		String formattedFromDate = formatters.format(fromDate);
		String formattedToDate = formatters.format(toDate);

		System.out.println("formattedFromDate----" + formattedFromDate);
		System.out.println("formattedToDate------" + formattedToDate);

//		LocalDateTime dateTime = 

//		2018-07-13T11:15:28.416Z

//		formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'z'");
//		strDate = formatter.format(date);
//		System.out.println(strDate);

//        String date_string = "6/23/2023 12:00:00 AM";
//        String date_format = "%m/%d/%Y %I:%M:%S %p";
//
//        		date_object = datetime.strptime(date_string, date_format)
//        		print(date_object)

//		String dateStr = "6/23/2023 01:00:00 PM";
		String dateFormat = "MM/dd/yyyy hh:mm:ss a";
//		Date date = new Date();
//
//		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
//		try {
//			date = sdf.parse(dateStr);
//			System.out.println(date);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		String dateStr1 = "6/23/2023 01:00:00 PM";
//		 String inputDateStr = "08/07/2023 12:18:35 PM";
//	        String inputDateFormat = "MM/dd/yyyy hh:mm:ss a";
		String outputDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

		SimpleDateFormat inputSdf = new SimpleDateFormat(dateFormat);
		SimpleDateFormat outputSdf = new SimpleDateFormat(outputDateFormat);

		try {
			Date date1 = inputSdf.parse(dateStr1);
			String outputDateStr = outputSdf.format(date1);
			System.out.println(outputDateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("   ");
		System.out.println("   ");
		
		String okTitle = "roceed";
		String output = okTitle.substring(0, 1).toUpperCase() + okTitle.substring(1);
		System.out.println(okTitle);
		System.out.println(output);
		
		
//		HazelcastConfig.getInstance().getErrorCount().put("errorData", testModel);
		
//		System.out.println(HazelcastConfig.getInstance().getErrorCount().get("errorData"));
		
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");
		
		String value = "5";
		boolean ret = isNumeric(value);
		
		System.out.println("ret -- "+ret);
		
		boolean retNum = isNumeric("5");
		
		System.out.println("ret -- "+retNum);
		
		String validity = "";
		int validityDay;
		
		if(ret) {
			validity = "GTD";
			validityDay = Integer.parseInt(value);
			System.out.println("validity -- "+validity);
			System.out.println("validityDay -- "+validityDay);
			
		} else {
			validity = "GTD";
			System.out.println("validity -- "+validity);
		}
		
		
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");
		System.out.println(" ");
		System.out.println("010");
		
		System.out.println(010);
		
		
		int count  = 50000;
		int cou = count/5;
		
		System.out.println(cou);


	}
	
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	
}
