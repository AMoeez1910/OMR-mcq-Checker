import ai.djl.util.passthrough.PassthroughNDArray;
import org.opencv.core.*;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.*;
import ai.djl.ndarray.NDArray;

import java.util.*;

public class demo {
    public demo() {
        Mat img = Imgcodecs.imread("3.png");
        Mat src = img.clone();
        //Imgcodecs.imwrite("00.jpg", img1);

        //convert img to gray
        Mat imggrey = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.cvtColor(src, imggrey, Imgproc.COLOR_BGR2GRAY);
        //Imgcodecs.imwrite("grey.jpg",imggrey);
        Mat imgblur = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.GaussianBlur(imggrey, imgblur, new org.opencv.core.Size(5, 5), 1);
        Mat imgcanny = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.Canny(imgblur, imgcanny, 10, 50);
        //Imgcodecs.imwrite("Canny.jpg",imggrey);
        HighGui.imshow("canny", imgcanny);
        HighGui.waitKey(0);
        Mat heirarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(imgcanny, contours, heirarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        Mat drawing = img.clone();
        for (int i = 0; i < contours.size(); i++) {
            Imgproc.drawContours(drawing, contours, i, new Scalar(0, 255, 0), 2, Imgproc.LINE_8, heirarchy, 0, new Point());
        }
        HighGui.imshow("canny", drawing);
        HighGui.waitKey(0);
        Rect rectangle = new Rect();
        List<MatOfPoint> rectangles = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > 50) {
                double perimeter = Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true);
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                double epsilon = 0.02 * perimeter;
                Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approxCurve, epsilon, true);
                if (approxCurve.rows() == 4)
                    rectangles.add(contours.get(i));
            }
        }
        Collections.sort(rectangles, new Comparator<MatOfPoint>() {
            public int compare(MatOfPoint contour1, MatOfPoint contour2) {
                double area1 = Imgproc.contourArea(contour1);
                double area2 = Imgproc.contourArea(contour2);
                return Double.compare(area2, area1);
            }
        });
        if (rectangles.size() != 0) {
            Imgproc.drawContours(src, rectangles, 0, new Scalar(0, 255, 0), 3);
            Imgproc.drawContours(src, rectangles, 1, new Scalar(0, 0, 255), 3);
        }

        ////////////////////////////////////
        if (rectangles.size() > 0) {
            // Convert the contour to a polygon
            MatOfPoint2f polygon = new MatOfPoint2f(rectangles.get(0).toArray());

            // Find the minimum area rectangle that encloses the polygon
            RotatedRect rect = Imgproc.minAreaRect(polygon);

            // Get the corner points of the rectangle
            Point[] corners = new Point[4];
            rect.points(corners);

            // Sort the corner points in clockwise order starting from the top-left corner
            Arrays.sort(corners, new Comparator<Point>() {
                public int compare(Point p1, Point p2) {
                    return Double.compare(p1.y, p2.y);
                }
            });
            Point topLeft = corners[0];
            Point bottomLeft = corners[1];
            Point bottomRight = corners[2];
            Point topRight = corners[3];
            if (topLeft.x > topRight.x) {
                Point temp = topLeft;
                topLeft = topRight;
                topRight = temp;
                temp = bottomLeft;
                bottomLeft = bottomRight;
                bottomRight = temp;
            }

            // Define the width and height of the bird's eye view
            double widthTop = Math.sqrt(Math.pow(topRight.x - topLeft.x, 2) + Math.pow(topRight.y - topLeft.y, 2));
            double widthBottom = Math.sqrt(Math.pow(bottomRight.x - bottomLeft.x, 2) + Math.pow(bottomRight.y - bottomLeft.y, 2));
            double maxWidth = Math.max(widthTop, widthBottom);
            int outputWidth = (int) maxWidth;

            double heightLeft = Math.sqrt(Math.pow(topLeft.x - bottomLeft.x, 2) + Math.pow(topLeft.y - bottomLeft.y, 2));
            double heightRight = Math.sqrt(Math.pow(topRight.x - bottomRight.x, 2) + Math.pow(topRight.y - bottomRight.y, 2));
            double maxHeight = Math.max(heightLeft, heightRight);
            int outputHeight = (int) maxHeight;

            // Define the destination points for warping the perspective
            Point[] srcPoints = {topLeft, topRight, bottomRight, bottomLeft};
            Point[] dstPoints = {new Point(0, 0), new Point(outputWidth - 1, 0), new Point(outputWidth - 1, outputHeight - 1), new Point(0, outputHeight - 1)};
            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(new MatOfPoint2f(srcPoints), new MatOfPoint2f(dstPoints));

            // Warp the perspective of the image
            Mat birdEyeView = new Mat(outputHeight, outputWidth, img.type());
            Imgproc.warpPerspective(img, birdEyeView, perspectiveTransform, new Size(outputWidth, outputHeight));
            // Display the bird's eye view image
            HighGui.imshow("Bird's Eye View", birdEyeView);
            HighGui.waitKey();
        } else {
            System.out.println("No rectangle contours found.");
        }
            HighGui.imshow("CONY", src);
            HighGui.waitKey(0);
        }
    public static void main(String[]args){
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        demo dem = new demo();
    }
}