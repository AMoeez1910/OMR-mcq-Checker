//package org.example;
//import org.opencv.core.*;
//import org.opencv.highgui.HighGui;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import java.util.*;
//
//public class Project {
//    public class Project() {
//        nu.pattern.OpenCV.loadShared();
//        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
//        int questions = 5;
//        int answers = 5;
//        List<Double> ans = new ArrayList<>();
//        ans.add(1.0);
//        ans.add(2.0);
//        ans.add(0.0);
//        ans.add(1.0);
//        ans.add(4.0);
//        //System.out.println(ans);
//        // Load the image
//        Mat src = Imgcodecs.imread("1.png");
//        Mat image = src.clone();
//        Mat imagewarpgrey = src.clone();
//        Mat img = src.clone();
//        Mat imgThresh = src.clone();
//        // Convert the image to grayscale
//        Mat gray = new Mat();
//        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
//
//        // Apply binary thresholding
//        Mat thresh = new Mat();
//        Imgproc.threshold(gray, thresh, 200, 255, Imgproc.THRESH_BINARY);
//
//        // Find contours
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//
//        // Sort the contours in descending order of their areas
//        Collections.sort(contours, new Comparator<MatOfPoint>() {
//            @Override
//            public int compare(MatOfPoint o1, MatOfPoint o2) {
//                double area1 = Imgproc.contourArea(o1);
//                double area2 = Imgproc.contourArea(o2);
//                return Double.compare(area2, area1);
//            }
//        });
//
//        // Find the largest rectangle contour
//        MatOfPoint largestContour = null;
//        MatOfPoint2f largestRect = new MatOfPoint2f();
//        MatOfPoint secondLargest = null;
//        MatOfPoint2f secondLargestRect = new MatOfPoint2f();
//        MatOfPoint thirdLargest = null;
//        MatOfPoint2f thirdLargestRect = new MatOfPoint2f();
//        int i = 0;
//        for (MatOfPoint contour : contours) {
//            MatOfPoint2f approxCurve = new MatOfPoint2f();
//            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
//            double peri = Imgproc.arcLength(contour2f, true);
//            Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * peri, true);
//            if (approxCurve.total() == 4) {
//                if (Imgproc.isContourConvex(new MatOfPoint(approxCurve.toArray()))) {
//                    double area = Imgproc.contourArea(contour);
//                    if (area > 50 && i == 0) {
//                        largestContour = contour;
//                        largestRect = approxCurve;
//                    }
//                    if (area > 50 && i ==1) {
//                        secondLargest = contour;
//                        secondLargestRect = approxCurve;
//                    }
//                    if (area > 50 && i != 5) {
//                        thirdLargest = contour;
//                        thirdLargestRect = approxCurve;
//                    }
//                    if (i == 5)
//                        break;
//                    i++;
//                }
//            }
//        }
//        Imgproc.drawContours(image, Collections.singletonList(largestContour), 0, new Scalar(0, 255, 0), 3);
//        Imgproc.drawContours(image, Collections.singletonList(secondLargest), -1, new Scalar(0, 0, 255), 3);
//        Imgproc.drawContours(image, Collections.singletonList(thirdLargest), 0, new Scalar(255, 0, 0), 3);
//        HighGui.imshow("MAIN", image);
//         HighGui.waitKey(0);
//        //Imgproc.drawContours(image, rectangles, 1, new Scalar(0, 0, 255), 3);
//
//        // Warp the perspective to bird's eye view
//        Mat warped = warp(largestRect, image,1);
//        Mat secwarped = warp(secondLargestRect, image,1);
//        Mat thirdwarped = warp(thirdLargestRect, image,1);
//        Mat rotatedImage = new Mat();
//        Core.transpose(thirdwarped, rotatedImage);
//        Core.flip(rotatedImage, rotatedImage, 0);
//        // Save the warped image
//        HighGui.imshow("output.jpg", warped);
//        HighGui.waitKey(0);
//        HighGui.imshow("output2.jpg", secwarped);
//        HighGui.waitKey(0);
//        HighGui.imshow("output2.jpg", rotatedImage);
//        HighGui.waitKey(0);
//
//        ///// marking applied
//        Imgproc.cvtColor(warped, imagewarpgrey, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.threshold(imagewarpgrey, imgThresh, 170, 255, Imgproc.THRESH_BINARY_INV);
//        HighGui.imshow("warp", warped);
//        HighGui.waitKey(0);
//        HighGui.imshow("greywarp", imagewarpgrey);
//        HighGui.waitKey(0);
//        HighGui.imshow("thresh", imgThresh);
//        HighGui.waitKey(0);
//        ////vertical split
//        // Split the image horizontally
//        List<Mat> horizontalSplits = new ArrayList<>();
//        int numOfHorzParts = 5; // Change this to adjust the number of horizontal splits
//        for (int j = 0; j < numOfHorzParts; j++) {
//            int startY = j * imgThresh.rows() / numOfHorzParts;
//            int endY = (j + 1) * imgThresh.rows() / numOfHorzParts;
//            if (j == numOfHorzParts - 1) {
//                endY = imgThresh.rows();
//            }
//            Mat horzPart = new Mat(imgThresh, new Rect(0, startY, imgThresh.cols(), endY - startY));
//            horizontalSplits.add(horzPart);
////            HighGui.imshow("Hsplit",horzPart);
////            HighGui.waitKey(0);
//        }
//        //            HighGui.imshow("Hsplit",horizontalSplits.get(0));
////            HighGui.waitKey(0);
//        //Split each row vertically
//        List<Mat> verticalSplits = new ArrayList<>();
//        int numOfVertParts = 5; // Change this to adjust the number of vertical splits
//        for (Mat part : horizontalSplits) {
//            for (int x = 0; x < numOfVertParts; x++) {
//                int startX = x * part.cols() / numOfVertParts;
//                int endX = (x + 1) * part.cols() / numOfVertParts;
//                Mat vertPart = new Mat(part, new Rect(startX, 0, endX - startX, part.rows()));
//                verticalSplits.add(vertPart);
////                HighGui.imshow("Vsplit",vertPart);
////                HighGui.waitKey(0);
//            }
//            // Add the final subpart (in case numCols is not evenly divisible by numOfVertParts)
//            // int startX = numCols - numCols / numOfParts;
//            // Mat vertPart = new Mat(part, new Rect(startX, 0, numCols - startX, part.rows()));
//        }
//        Mat Myppixelval = new Mat(questions, answers, CvType.CV_8U, Scalar.all(0));
//        //System.out.println(Myppixelval.dump());
//        int countC = 0;
//        int countR = 0;
//        for (int j=0;j<5;j++) {
//            for (int x = 0; x < 50; x++)
//                verticalSplits.get(0).put(0, x, 0);
//        }
//        //System.out.println(verticalSplits.toArray());
//        for (Mat pic : verticalSplits) {
//            //HighGui.imshow("pic",pic);
//            int totalPixels = Core.countNonZero(pic);
//            //System.out.println(totalPixels);
//            Myppixelval.put(countR, countC, totalPixels/5);
//            countC += 1;
//            if (countC == answers) {
//                countC = 0;
//                countR += 1;
//            }
//            //System.out.println(verticalSplits.size());
//        }
//        //System.out.println(Myppixelval.dump());
//        ArrayList<Double> locsList = new ArrayList<Double>();
//        for (int x =0;x<questions;x++){
//            Mat arr = Myppixelval.row(x);
//            Core.MinMaxLocResult result = Core.minMaxLoc(arr);
//            locsList.add(result.maxLoc.x);
//        }
//        ArrayList<Integer> grade = new ArrayList<>();
//        for(int x=0;x<questions;x++){
//            if (locsList.get(x).equals(ans.get(x))){
//                grade.add(1);
//            }
//            else{
//                grade.add(0);
//            }
//        }
//        double sum = grade.stream().mapToInt(Integer::intValue).sum();
//        double score = (sum/questions)*100;
//        System.out.println("Your answers:\t"+locsList);
//        System.out.println("Correct answers:\t"+ans);
//        System.out.println("Your score:\t"+sum);
//        System.out.println("Your Percentage:\t"+score);
//        //System.out.println(locsList);
//        Mat imageresult = warped.clone();
//        Size size = imageresult.size();
//        int secW=(int)(size.width/questions);
//        int secH=(int)(size.height/answers);
//        for (int x=0;x<questions;x++){
//            int myAns =locsList.get(x).intValue();
//            int cX = (myAns*secW)+secW/2;
//            int cY = (x*secH)+secH/2;
//            int Ms = ans.get(x).intValue();
//            int cXcorr = (Ms*secW)+secW/2;
//            Imgproc.circle(imageresult,new Point(cXcorr,cY),15,new Scalar(255,0,0),Imgproc.FILLED);
//            if(ans.get(x).equals(locsList.get(x))){
//                Imgproc.circle(imageresult,new Point(cX,cY),15,new Scalar(0,255,0),Imgproc.FILLED);
//            }
//            else{
//                Imgproc.circle(imageresult,new Point(cX,cY),15,new Scalar(0,0,255),Imgproc.FILLED);
//            }
//        }
//        HighGui.imshow("Result",imageresult);
//        HighGui.waitKey(0);
//        Mat imRawDraw= Mat.zeros(warped.rows(), warped.cols(), CvType.CV_8UC3);
//        for (int x=0;x<questions;x++){
//            int myAns =locsList.get(x).intValue();
//            int cX = (myAns*secW)+secW/2;
//            int cY = (x*secH)+secH/2;
//            int Ms = ans.get(x).intValue();
//            int cXcorr = (Ms*secW)+secW/2;
//            Imgproc.circle(imRawDraw,new Point(cXcorr,cY),15,new Scalar(255,0,0),Imgproc.FILLED);
//            if(ans.get(x).equals(locsList.get(x))){
//                Imgproc.circle(imRawDraw,new Point(cX,cY),15,new Scalar(0,255,0),Imgproc.FILLED);
//            }
//            else{
//                Imgproc.circle(imRawDraw,new Point(cX,cY),15,new Scalar(0,0,255),Imgproc.FILLED);
//            }
//        }
//        HighGui.imshow("Result",imRawDraw);
//        HighGui.waitKey(0);
//        Mat imginv = warp(largestRect,imRawDraw,2);
//        HighGui.imshow("Result",imginv);
//        HighGui.waitKey(0);
//        Mat imgFinal = image.clone();
//        Mat imgfin = new Mat();
//        //display results
//
//    }
//    public static Mat warp(MatOfPoint2f largestRect, Mat image,int choice) {
//        double widthTop = Math.sqrt(Math.pow(largestRect.toArray()[1].x - largestRect.toArray()[0].x, 2) + Math.pow(largestRect.toArray()[1].y - largestRect.toArray()[0].y, 2));
//        double widthBottom = Math.sqrt(Math.pow(largestRect.toArray()[2].x - largestRect.toArray()[3].x, 2) + Math.pow(largestRect.toArray()[2].y - largestRect.toArray()[3].y, 2));
//        double maxWidth = Math.max(widthTop, widthBottom);
//
//        double heightLeft = Math.sqrt(Math.pow(largestRect.toArray()[3].x - largestRect.toArray()[0].x, 2) + Math.pow(largestRect.toArray()[3].y - largestRect.toArray()[0].y, 2));
//        double heightRight = Math.sqrt(Math.pow(largestRect.toArray()[2].x - largestRect.toArray()[1].x, 2) + Math.pow(largestRect.toArray()[2].y - largestRect.toArray()[1].y, 2));
//        double maxHeight = Math.max(heightLeft, heightRight);
//
//        Mat dest = new Mat(4, 2, CvType.CV_32FC1);
//        dest.put(0, 0, new double[]{0, 0});
//        dest.put(1, 0, new double[]{maxWidth - 1, 0});
//        dest.put(2, 0, new double[]{maxWidth - 1, maxHeight - 1});
//        dest.put(3, 0, new double[]{0, maxHeight - 1});
//        if(choice ==1){
//            return warping(maxWidth,maxHeight,dest,largestRect,image);
//
//        }
//        else if(choice ==2){
//            return rewarping(maxWidth,maxHeight,dest,largestRect,image);
//        }
//        else {
//            return null;
//        }
//    }
//    public static Mat warping (double maxWidth,double maxHeight,Mat dest,MatOfPoint2f largestRect, Mat image){
//        Mat transform = Imgproc.getPerspectiveTransform(largestRect, dest);
//        Mat warped = new Mat();
//        Imgproc.warpPerspective(image, warped, transform, new Size(maxWidth, maxHeight));
//        return warped;
//    }
//    public static Mat rewarping (double maxWidth,double maxHeight,Mat dest,MatOfPoint2f largestRect, Mat image){
//        Mat transform = Imgproc.getPerspectiveTransform(dest,largestRect);
//        Mat rewarped = new Mat();
//        Imgproc.warpPerspective(image, rewarped, transform, new Size(maxWidth, maxHeight));
//        return rewarped;
//    }
//}