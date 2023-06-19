package org.example;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class OMR extends JFrame {

    private JLabel labelName;
    private JLabel labelResult;
    private JLabel labelScore;
    private JLabel labelScore1;
    private JLabel labelpercent;
    private ImageIcon ImgName;
    private ImageIcon ImgResult;
    private ImageIcon ImgScore;
    private ImageIcon pass = new ImageIcon("pass.png");
    private ImageIcon fail = new ImageIcon("fail.png");
    public OMR() {
        super("RESULT");
        nu.pattern.OpenCV.loadShared();
//        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        int questions = 5;
        int answers = 5;
        double a=0.0;
        double b=1.0;
        double c=2.0;
        double d=3.0;
        double e=4.0;

        List<Double> ans = new ArrayList<>();
        ////ans key A////////
        ans.add(b);
        ans.add(c);
        ans.add(d);
        ans.add(e);
        ans.add(a);

        //System.out.println(ans);
        // Load the image
       // Mat orig = Imgcodecs.imread("omr4.png");
        Mat src = Imgcodecs.imread("omrBilal.jpg");
        Imgproc.resize(src,src,new Size(700,1024));
        Mat image = src.clone();
        Mat imagewarpgrey = src.clone();
        Mat img = src.clone();
        Mat imgThresh = src.clone();
        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply binary thresholding
        Mat thresh = new Mat();
        Imgproc.threshold(gray, thresh, 200, 255, Imgproc.THRESH_BINARY);
//        HighGui.imshow("MAIN", image);
//         HighGui.waitKey(0);
        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//        HighGui.imshow("MAIN", thresh);
//        HighGui.waitKey(0);
        // Sort the contours in descending order of their areas
        Collections.sort(contours, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint o1, MatOfPoint o2) {
                double area1 = Imgproc.contourArea(o1);
                double area2 = Imgproc.contourArea(o2);
                return Double.compare(area2, area1);
            }
        });

        // Find the largest rectangle contour
        MatOfPoint largestContour = null;
        MatOfPoint2f largestRect = new MatOfPoint2f();
        MatOfPoint secondLargest = null;
        MatOfPoint2f secondLargestRect = new MatOfPoint2f();
        MatOfPoint thirdLargest = null;
        MatOfPoint2f thirdLargestRect = new MatOfPoint2f();
        int i = 0;
        for (MatOfPoint contour : contours) {
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double peri = Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * peri, true);
            if (approxCurve.total() == 4) {
                if (Imgproc.isContourConvex(new MatOfPoint(approxCurve.toArray()))) {
                    double area = Imgproc.contourArea(contour);
                    if (area > 50 && i == 1) {
                        largestContour = contour;
                        largestRect = approxCurve;
                    }
                    if (area > 50 && i ==3) {
                        secondLargest = contour;
                        secondLargestRect = approxCurve;
                    }
                    if (area > 50 && i !=6) {
                        thirdLargest = contour;
                        thirdLargestRect = approxCurve;
                    }
                    if (i == 6)
                        break;
                    i++;
                }
            }
        }
        Imgproc.drawContours(img, Collections.singletonList(largestContour), 0, new Scalar(0, 255, 0), 3);
        Imgproc.drawContours(img, Collections.singletonList(secondLargest), -1, new Scalar(0, 0, 255), 3);
        Imgproc.drawContours(img, Collections.singletonList(thirdLargest), 0, new Scalar(255, 0, 0), 3);
        HighGui.imshow("MAIN", img);
         HighGui.waitKey(0);
//        Imgproc.drawContours(image, rectangles, 1, new Scalar(0, 0, 255), 3);

        // Warp the perspective to bird's eye view
        Mat warped = warp(largestRect, image,1);
        Mat secwarped = warp(secondLargestRect, image,1);
        Mat thirdwarped = warp(thirdLargestRect, image,1);
        Size prize = thirdwarped.size();
        double height=prize.height;
        double width=prize.width;
        System.out.println(height);
        System.out.println(width);
        if(height>200.0 && width>=10)
            Core.rotate(thirdwarped,thirdwarped,Core.ROTATE_90_CLOCKWISE);
        System.out.println("dfufhsjf");

        Size rotate = secwarped.size();
        double height1=rotate.height;
        double width1=rotate.width;
        System.out.println(height1);
        System.out.println(width1);
        if(height1>180 && width1>50)
            Core.rotate(secwarped,secwarped,Core.ROTATE_90_CLOCKWISE);
        /////////////////////////////
        Size rotate1 = warped.size();
        double height2=rotate1.height;
        double width2=rotate1.width;
        System.out.println(height2);
        System.out.println(width2);
        if(height2>340 && width2>290)
            Core.rotate(warped,warped,Core.ROTATE_90_CLOCKWISE);

        //Core.rotate(thirdwarped,thirdwarped,Core.ROTATE_90_CLOCKWISE);
        // Save the warped image

//        HighGui.imshow("output.jpg", thirdwarped);
//        HighGui.waitKey(0);

//        HighGui.imshow("output2.jpg", rotatedImage);
     //   HighGui.waitKey(0);

        MatOfByte buffer2 = new MatOfByte();
        Imgcodecs.imencode(".png", thirdwarped, buffer2);
        byte[] bytes2 = buffer2.toArray();
        /////////////////
        JPanel panel = new JPanel(new BorderLayout());
        /////////
        ImgName = new ImageIcon(bytes2);
        ///////////////
        labelName = new JLabel("Name:");
        labelResult = new JLabel("Result:");
        labelScore = new JLabel("Score:");
        labelpercent = new JLabel("Percentage:");
        ///////
        JLabel imageName = new JLabel(ImgName);
        JLabel imageScore = new JLabel(ImgScore);
        ///////////
        labelName.setBounds(10,10,100,20);
        labelName.setFont(new Font("Arial", Font.PLAIN, 25));
        imageName.setBounds(40,10,600,20);


        labelResult.setBounds(10,100,100,20);
        labelResult.setFont(new Font("Arial", Font.PLAIN, 25));

        labelScore.setBounds(10,450,100,20);
        labelScore.setFont(new Font("Arial", Font.PLAIN, 25));

        labelpercent.setBounds(10,540,130,30);
        labelpercent.setFont(new Font("Arial", Font.PLAIN, 25));
        ///////////
        add(labelName);
        add(imageName);
        add(labelResult);
        add(labelScore);
        add(labelpercent);
//        JLabel imageName = new JLabel(ImgName);
//        JLabel imageName = new JLabel(ImgName);

        ///// marking applied
        Imgproc.cvtColor(warped, imagewarpgrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(imagewarpgrey, imgThresh, 170, 255, Imgproc.THRESH_BINARY_INV);

//        HighGui.imshow("greywarp", imagewarpgrey);
//        HighGui.waitKey(0);
//        HighGui.imshow("thresh", imgThresh);
//        HighGui.waitKey(0);
        ////vertical split
        // Split the image horizontally
        List<Mat> horizontalSplits = new ArrayList<>();
        int numOfHorzParts = 5; // Change this to adjust the number of horizontal splits
        for (int j = 0; j < numOfHorzParts; j++) {
            int startY = j * imgThresh.rows() / numOfHorzParts+6;
            int endY = (j + 1) * imgThresh.rows() / numOfHorzParts-4;
            if (j == numOfHorzParts - 1) {
                endY = imgThresh.rows()-7;
            }
            Mat horzPart = new Mat(imgThresh, new Rect(0, startY, imgThresh.cols(), endY - startY));
            horizontalSplits.add(horzPart);
//            HighGui.imshow("Hsplit",horzPart);
//            HighGui.waitKey(0);
        }
//        System.out.println("Moee");
//        System.out.println(horizontalSplits.size());
//            HighGui.imshow("Hsplit",horizontalSplits.get(0));
//            HighGui.waitKey(0);
        //Split each row vertically
        List<Mat> verticalSplits = new ArrayList<>();
        int numOfVertParts = 5; // Change this to adjust the number of vertical splits
        for (Mat part : horizontalSplits) {
            for (int x = 0; x < numOfVertParts; x++) {
                int startX = x * part.cols() / numOfVertParts+6;
                int endX = (x + 1) * part.cols() / numOfVertParts-4;
                Mat vertPart = new Mat(part, new Rect(startX, 0, endX - startX, part.rows()));
                verticalSplits.add(vertPart);
//                HighGui.imshow("Vsplit",vertPart);
//                HighGui.waitKey(0);
            }
            // Add the final subpart (in case numCols is not evenly divisible by numOfVertParts)
            // int startX = numCols - numCols / numOfParts;
            // Mat vertPart = new Mat(part, new Rect(startX, 0, numCols - startX, part.rows()));
        }
        Mat Myppixelval = new Mat(questions, answers, CvType.CV_8U, Scalar.all(0));
        //System.out.println(Myppixelval.dump());
        int countC = 0;
        int countR = 0;
        for (int j=0;j<5;j++) {
            for (int x = 0; x < 50; x++)
                verticalSplits.get(0).put(0, x, 0);
        }
        System.out.println(verticalSplits.toArray());
        for (Mat pic : verticalSplits) {
            //HighGui.imshow("pic",pic);
            //System.out.println(pic.dump());
            int totalPixels = Core.countNonZero(pic);
            //System.out.println(totalPixels);
            Myppixelval.put(countR, countC, totalPixels/5);
            countC += 1;
            if (countC == answers) {
                countC = 0;

                countR += 1;
            }
        }
        //System.out.println(verticalSplits.size());
        System.out.println(Myppixelval.dump());
        ArrayList<Double> locsList = new ArrayList<Double>();
        int count;
        int row=1;
        for (int x =0;x<questions;x++){
            count=0;
            for (int y = 0; y < answers; y++) {
                double[] nonzeros = Myppixelval.get(x, y);
                 if (row > questions*4) {
                    if (nonzeros[0] >= 220.0) {
                        count += 1;
                    }
                }
                else {
                    if ((nonzeros[0] >= 220.0)) {
                        count += 1;
                    }

                }

                row+=1;
            }
            System.out.println(count);
            if(count == 1) {
                Mat arr = Myppixelval.row(x);
                Core.MinMaxLocResult result = Core.minMaxLoc(arr);
                locsList.add(result.maxLoc.x);
            }
            else {
                locsList.add(-1.0);
            }
        }
        ArrayList<Integer> grade = new ArrayList<>();
        for(int x=0;x<questions;x++){
            if (locsList.get(x).equals(ans.get(x))){
                grade.add(1);
            }
            else{
                grade.add(0);
            }
        }
        double sum = grade.stream().mapToInt(Integer::intValue).sum();
        double score = (sum/questions)*100;
        ArrayList<String> mark = new ArrayList<>();
        for (int x=0;x<questions;x++){
            if(ans.get(x)==0.0)
                mark.add("A");
            else if(ans.get(x)==1.0)
                mark.add("B");
            else if(ans.get(x)==2.0)
                mark.add("C");
            else if(ans.get(x)==3.0)
                mark.add("D");
            else
                mark.add("E");
        }
        System.out.println("Your answers:\t"+locsList);
        System.out.println("Correct answers:\t"+mark);
        System.out.println("Your score:\t"+sum);
        System.out.println("Your Percentage:\t"+score);
        //System.out.println(locsList);
        Imgproc.putText(secwarped,Double.toString(score)+"%", new Point(60, 50),Imgproc.FONT_HERSHEY_COMPLEX,1,new Scalar(0,0,0),3);
//        HighGui.imshow("output2.jpg", secwarped);
//        HighGui.waitKey(0);

        labelScore1 = new JLabel(Integer.toString((int)sum)+" out of 5");
        labelScore1.setBounds(90,450,180,30);
        labelScore1.setFont(new Font("Arial", Font.PLAIN, 30));
        labelScore1.setForeground(Color.RED);
        add(labelScore1);
        ///////////
        MatOfByte buffer1 = new MatOfByte();
        Imgcodecs.imencode(".png", secwarped, buffer1);
        byte[] bytes1 = buffer1.toArray();
        ImgScore = new ImageIcon(bytes1);
        JLabel Imagescore = new JLabel(ImgScore);
        Imagescore.setBounds(150,410,300,300);
        add(Imagescore);
        ///////////
        if (score>50){
            JLabel PASS = new JLabel(pass);
            PASS.setBounds(600,500,300,300);
            add(PASS);
        }
        else {
            JLabel FAIL = new JLabel(fail);
            FAIL.setBounds(600,500,300,300);
            add(FAIL);
        }


        Mat imageresult = warped.clone();
        Size size = imageresult.size();
        int secW=(int)(size.width/questions);
        int secH=(int)(size.height/answers);
        for (int x=0;x<questions;x++){

            int myAns =locsList.get(x).intValue();
            int cX = (myAns*secW)+secW/2;
            int cY = (x*secH)+secH/2;
            int Ms = ans.get(x).intValue();
            int cXcorr = (Ms*secW)+secW/2;
            Imgproc.ellipse(imageresult,new RotatedRect(new Point(cXcorr, cY), new Size(50 ,50), 180),new Scalar(255,0,0),3);
            if((locsList.get(x)!=-1.0)) {
                if (ans.get(x).equals(locsList.get(x))) {
                    Imgproc.ellipse(imageresult,new RotatedRect(new Point(cX, cY),new Size(50 ,50), 180),new Scalar(0,255,0),3);
                } else {
                    Imgproc.ellipse(imageresult,new RotatedRect(new Point(cX, cY), new Size(50 ,50), 180),new Scalar(0,0,255),3);
                }
            }
        }
        /////////////
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", imageresult, buffer);
        byte[] bytes = buffer.toArray();
        ImgResult = new ImageIcon(bytes);
        JLabel imageResult = new JLabel(ImgResult);
        imageResult.setBounds(160,10,600,500);
        add(imageResult);
        ////////////
//        HighGui.imshow("Result",imageresult);
//        HighGui.waitKey(0);
        //HighGui.imshow("Result",imRawDraw);
        //HighGui.waitKey(0);
        //HighGui.imshow("Result",imginv);
        //HighGui.waitKey(0);
        Mat imgFinal = image.clone();
        Mat imgfin = new Mat();
       //display results
        labelScore1=new JLabel(
                Double.toString(sum));
        add(labelScore1);

        setLayout(null);
        setSize(900, 800); // set size of frame
        getContentPane().setBackground(Color.gray);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center frame
        setVisible(true);
    }
    public static Mat warp(MatOfPoint2f largestRect, Mat image,int choice) {
        double widthTop = Math.sqrt(Math.pow(largestRect.toArray()[1].x - largestRect.toArray()[0].x, 2) + Math.pow(largestRect.toArray()[1].y - largestRect.toArray()[0].y, 2));
        double widthBottom = Math.sqrt(Math.pow(largestRect.toArray()[2].x - largestRect.toArray()[3].x, 2) + Math.pow(largestRect.toArray()[2].y - largestRect.toArray()[3].y, 2));
        double maxWidth = Math.max(widthTop, widthBottom);

        double heightLeft = Math.sqrt(Math.pow(largestRect.toArray()[3].x - largestRect.toArray()[0].x, 2) + Math.pow(largestRect.toArray()[3].y - largestRect.toArray()[0].y, 2));
        double heightRight = Math.sqrt(Math.pow(largestRect.toArray()[2].x - largestRect.toArray()[1].x, 2) + Math.pow(largestRect.toArray()[2].y - largestRect.toArray()[1].y, 2));
        double maxHeight = Math.max(heightLeft, heightRight);

        Mat dest = new Mat(4, 2, CvType.CV_32FC1);
        dest.put(0, 0, new double[]{0, 0});
        dest.put(1, 0, new double[]{maxWidth - 1, 0});
        dest.put(2, 0, new double[]{maxWidth - 1, maxHeight - 1});
        dest.put(3, 0, new double[]{0, maxHeight - 1});
        if(choice ==1){
            return warping(maxWidth,maxHeight,dest,largestRect,image);

        }
        else {
            return null;
        }
    }
    public static Mat warping (double maxWidth,double maxHeight,Mat dest,MatOfPoint2f largestRect, Mat image){
        Mat transform = Imgproc.getPerspectiveTransform(largestRect, dest);
        Mat warped = new Mat();
        Imgproc.warpPerspective(image, warped, transform, new Size(maxWidth, maxHeight));
        return warped;
    }
    public static void main(String[]args){
        OMR omr = new OMR();

    }
}