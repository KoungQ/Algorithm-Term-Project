package algorithm.termproject;

import java.util.*;

public class CampusShortestPathFinder {
    private static final int INF = 1000000; // 연결되지 않은 노드를 나타내기 위한 값
    private static double[][] distance; // 거리 행렬
    private static int[][] next; // 경로 재구성을 위한 행렬
    private static String[] locations = {
            "비전타워", "정문", "전자정보도서관", "가천관", "중앙도서관", "AI공학관"
    };
    private static int[][] coordinates = {
            {0, 0}, {-1, 0}, {-3, 2}, {-2, 4}, {-8, 8}, {-10, 10}
    };

    public static void main(String[] args) {
        int numLocations = locations.length;
        distance = new double[numLocations][numLocations];
        next = new int[numLocations][numLocations];

        initializeGraph(numLocations);
        floydWarshall(numLocations);

        Scanner scanner = new Scanner(System.in);

        System.out.println("등록된 장소:");
        for (int i = 0; i < numLocations; i++) {
            System.out.printf("%d: %s (%d, %d)\n", i, locations[i], coordinates[i][0], coordinates[i][1]);
        }

        System.out.print("출발지를 선택하세요 (0~" + (numLocations - 1) + "): ");
        int start = scanner.nextInt();
        System.out.print("목적지를 선택하세요 (0~" + (numLocations - 1) + "): ");
        int end = scanner.nextInt();

        System.out.print("경유지 개수를 입력하세요: ");
        int numWaypoints = scanner.nextInt();
        List<Integer> waypoints = new ArrayList<>();
        for (int i = 0; i < numWaypoints; i++) {
            System.out.print("경유지 번호를 입력하세요 (0~" + (numLocations - 1) + "): ");
            waypoints.add(scanner.nextInt());
        }

        List<Integer> fullPath = constructPathWithWaypoints(start, waypoints, end);
        printFullPath(fullPath);
        calculateTravelDetails(fullPath);

        scanner.close();
    }

    private static void initializeGraph(int numLocations) {
        for (int i = 0; i < numLocations; i++) {
            for (int j = 0; j < numLocations; j++) {
                if (i == j) {
                    distance[i][j] = 0;
                } else {
                    distance[i][j] = calculateDistance(coordinates[i], coordinates[j]) * 100; // 100m 기준
                }
                next[i][j] = j;
            }
        }
    }

    private static double calculateDistance(int[] point1, int[] point2) {
        return Math.sqrt(Math.pow(point1[0] - point2[0], 2) + Math.pow(point1[1] - point2[1], 2));
    }

    private static void floydWarshall(int numLocations) {
        for (int k = 0; k < numLocations; k++) {
            for (int i = 0; i < numLocations; i++) {
                for (int j = 0; j < numLocations; j++) {
                    if (distance[i][k] + distance[k][j] < distance[i][j]) {
                        distance[i][j] = distance[i][k] + distance[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }

    private static List<Integer> constructPathWithWaypoints(int start, List<Integer> waypoints, int end) {
        List<Integer> fullPath = new ArrayList<>();
        int current = start;
        for (int waypoint : waypoints) {
            addPath(current, waypoint, fullPath);
            current = waypoint;
        }
        addPath(current, end, fullPath);
        return fullPath;
    }

    private static void addPath(int start, int end, List<Integer> fullPath) {
        if (distance[start][end] == INF) {
            System.out.println("경로가 존재하지 않습니다.");
            return;
        }
        while (start != end) {
            if (fullPath.isEmpty() || fullPath.get(fullPath.size() - 1) != start) {
                fullPath.add(start);
            }
            start = next[start][end];
        }
        if (fullPath.isEmpty() || fullPath.get(fullPath.size() - 1) != end) {
            fullPath.add(end);
        }
    }

    private static void printFullPath(List<Integer> fullPath) {
        System.out.print("최단 경로: ");
        for (int i = 0; i < fullPath.size(); i++) {
            System.out.print(locations[fullPath.get(i)]);
            if (i < fullPath.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    private static void calculateTravelDetails(List<Integer> fullPath) {
        double totalDistance = 0;
        for (int i = 0; i < fullPath.size() - 1; i++) {
            totalDistance += distance[fullPath.get(i)][fullPath.get(i + 1)];
        }

        double travelTime = totalDistance / 50; // 분당 50m 이동 (시속 3km 기준)
        double caloriesBurned = (totalDistance / 1000.0) * 50; // kcal 계산

        System.out.printf("총 거리: %.2f 미터\n", totalDistance);
        System.out.printf("예상 소요 시간: %.2f 분\n", travelTime);
        System.out.printf("소모 칼로리: %.2f kcal\n", caloriesBurned);
    }
}
