import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CourseListGenerator {

    public static void main(String[] args) throws IOException {
        String path = System.getProperty("user.dir") + "/resourses/courses.txt";
        HashMap<String, String> courseMap = new HashMap<>();
        HashMap<String, List<String>> dependencyMap = new HashMap<>();
        BufferedReader fileReader = new BufferedReader(new FileReader(path));
        try {
            readCourseList(courseMap, dependencyMap, fileReader);
        } finally {
            fileReader.close();
        }
        List<String> courseSequence = courseSequenceRecommendation(dependencyMap);
        returnRecommendedList(courseMap, courseSequence);
    }

    private static void readCourseList(HashMap<String, String> courseMap, HashMap<String, List<String>> courseDepMap,
                                       BufferedReader fileReader) throws IOException {

        String readLine;
        String currentCourse = null;
        while ((readLine = fileReader.readLine()) != null) {
            if (readLine.equals("")) {
                continue;
            }
            if (readLine.startsWith("HCDE")) {
                int index = readLine.indexOf(" ", 5);
                currentCourse = readLine.substring(5, index);
                courseMap.put(currentCourse, readLine.substring(index + 1));
            }
            if (readLine.startsWith("Course")) {
                int index = readLine.indexOf(":") + 1;
                if (readLine.substring(index).isEmpty()) {
                    courseDepMap.put(currentCourse, Collections.emptyList());
                    continue;
                }
                LinkedList list = new LinkedList();
                int index2 = readLine.indexOf(",", index);
                if (index2 == -1) {
                    if (readLine.substring(index).isEmpty()) {
                        continue;
                    } else {
                        list.add(readLine.substring(index));
                    }
                } else {
                    while (index < readLine.length() && index2 < readLine.length()) {
                        list.add(readLine.substring(index, index2));
                        index = index2 + 1;
                        index2 = readLine.indexOf(",", index);
                        if (index2 == -1) {
                            if (readLine.substring(index).isEmpty()) {
                                break;
                            } else {
                                list.add(readLine.substring(index));
                                break;
                            }
                        }
                    }

                }
                courseDepMap.put(currentCourse, list);
            }
        }
    }

    public static List<String> courseSequenceRecommendation(HashMap<String, List<String>> courseDepMap) {
        Collection<String> courseSequence = new LinkedHashSet<>();

        for (String courseId : courseDepMap.keySet()) {
            getAllSequence(courseId, courseDepMap, courseSequence);
        }
        return courseSequence.stream().collect(Collectors.toList());
    }

    public static void getAllSequence(String courseId, HashMap<String, List<String>> courseDepMap, Collection<String> courseSequence) {
        if (courseDepMap.get(courseId) == null) {
            if (!courseSequence.contains(courseId)) {
                courseSequence.add(courseId);
            }
            return;
        }
        Collection<String> depList = courseDepMap.get(courseId);
        for (String depId : depList) {
            getAllSequence(depId, courseDepMap, courseSequence);
        }
        if (!courseSequence.contains(courseId)) {
            courseSequence.add(courseId);
        }
    }

    public static void returnRecommendedList(HashMap<String, String> courseMap, Collection<String> courseSequence) {
        for (String courseId : courseSequence) {
            if (courseMap.containsKey(courseId)) {
                System.out.println(courseId + " " + courseMap.get(courseId));
            }
        }

    }
}