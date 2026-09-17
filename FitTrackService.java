import java.util.*;
public class FitTrackService {

    public static class User {
        private String username;
        private String password;
        private String name;
        private int age;
        private int heightCm;
        private double weightKg;
        private String memberSince;

        public User(String username, String password, String name, int age, int heightCm, double weightKg, String memberSince) {
            this.username = username;
            this.password = password;
            this.name = name;
            this.age = age;
            this.heightCm = heightCm;
            this.weightKg = weightKg;
            this.memberSince = memberSince;
        }

        public String getUsername(){
            return username; 
        }
        public String getPassword(){
            return password; 
        }
        public String getName(){
            return name;
        }
        public void setName(String name){ 
            this.name = name; 
        }
        public int getAge(){ 
            return age; 
        }
        public void setAge(int age){
            this.age = age;
        }
        public int getHeightCm(){
            return heightCm;
        }
        public void setHeightCm(int heightCm){
            this.heightCm = heightCm;
        }
        public double getWeightKg(){
            return weightKg;
        }
        public void setWeightKg(double weightKg){
            this.weightKg = weightKg;
        }
        public String getMemberSince(){
            return memberSince;
        }
        public double getBmi(){
            double heightM = heightCm / 100.0;
            return weightKg / (heightM * heightM);
        }
        public String getBmiStatus() {
            double bmi = getBmi();
            if (bmi < 18.5) return "Underweight";
            if (bmi < 25) return "Normal";
            if (bmi < 30) return "Overweight";
            return "Obese";
        }
    }


    public static class Goal {
        private String goalType;
        private double targetWeightKg;
        private int dailyStepTarget;

        public Goal(String goalType, double targetWeightKg, int dailyStepTarget) {
            this.goalType = goalType;
            this.targetWeightKg = targetWeightKg;
            this.dailyStepTarget = dailyStepTarget;
        }

        public String getGoalType(){
            return goalType;
        }
        public void setGoalType(String goalType){
            this.goalType = goalType;
        }
        public double getTargetWeightKg(){
            return targetWeightKg;
        }
        public void setTargetWeightKg(double targetWeightKg){
            this.targetWeightKg = targetWeightKg;
        }
        public int getDailyStepTarget(){
            return dailyStepTarget;
        }
        public void setDailyStepTarget(int dailyStepTarget){
            this.dailyStepTarget = dailyStepTarget;
        }

 
        public double getProgressPercent(double startWeightKg, double currentWeightKg) {
            double totalChange = targetWeightKg - startWeightKg;
            if (totalChange == 0) return 100;
            double doneChange = currentWeightKg - startWeightKg;
            double pct = (doneChange / totalChange) * 100.0;
            if (pct < 0) pct = 0;
            if (pct > 100) pct = 100;
            return pct;
        }
    }


    public static class DailyActivity {
        private String date;
        private int steps;
        private int stepGoal;
        private int calories;
        private int calorieGoal;
        private double waterLiters;
        private double waterGoalLiters;
        private int exerciseMinutes;
        private int exerciseGoalMinutes;

        public DailyActivity(String date, int steps, int stepGoal, int calories, int calorieGoal,
                              double waterLiters, double waterGoalLiters, int exerciseMinutes, int exerciseGoalMinutes) {
            this.date = date;
            this.steps = steps;
            this.stepGoal = stepGoal;
            this.calories = calories;
            this.calorieGoal = calorieGoal;
            this.waterLiters = waterLiters;
            this.waterGoalLiters = waterGoalLiters;
            this.exerciseMinutes = exerciseMinutes;
            this.exerciseGoalMinutes = exerciseGoalMinutes;
        }

        public String getDate(){
            return date;
        }
        public int getSteps(){
            return steps;
        }
        public void setSteps(int steps){
            this.steps = steps;
        }
        public int getStepGoal(){
            return stepGoal;
        }
        public int getCalories(){
            return calories;
        }
        public void setCalories(int calories){
            this.calories = calories;
        }
        public int getCalorieGoal(){
            return calorieGoal;
        }
        public double getWaterLiters(){
            return waterLiters;
        }
        public void setWaterLiters(double waterLiters){
            this.waterLiters = waterLiters;
        }
        public double getWaterGoalLiters(){
            return waterGoalLiters;
        }
        public int getExerciseMinutes(){
            return exerciseMinutes;
        }
        public void setExerciseMinutes(int exerciseMinutes){
            this.exerciseMinutes = exerciseMinutes;
        }
        public int getExerciseGoalMinutes(){
            return exerciseGoalMinutes;
        }

        public void addSteps(int amount){
            this.steps += amount;
        }
        public void addCalories(int amount){
            this.calories += amount;
        }
        public void addWater(double liters){
            this.waterLiters += liters;
        }
        public void addExercise(int minutes){
            this.exerciseMinutes += minutes;
        }
    }


    private Map<String, User> users = new HashMap<>();
    private User currentUser;
    private Goal currentGoal;
    private double startWeightKg;
    private List<DailyActivity> history = new ArrayList<>();
    private DailyActivity today;

    public FitTrackService() {
        seedDemoData();
    }

    private void seedDemoData() {
        User demo = new User("user", "1234", "User", 19, 180, 58, "01 May 2026");
        users.put(demo.getUsername(), demo);

        today = new DailyActivity("Today", 9500, 10000, 2200, 2500, 2.5, 3.0, 45, 60);
        history.add(today);
        history.add(new DailyActivity("Yesterday", 8700, 10000, 2100, 2500, 2.0, 3.0, 30, 60));
        history.add(new DailyActivity("2 days ago", 10200, 10000, 2400, 2500, 3.0, 3.0, 60, 60));
    }

    public boolean login(String username, String password) {
        User u = users.get(username);
        if (u != null && u.getPassword().equals(password)) {
            currentUser = u;
            startWeightKg = u.getWeightKg();
            currentGoal = new Goal("Lose Weight", 65, 10000);
            return true;
        }
        return false;
    }

    public boolean register(String username, String password, String name) {
        if (users.containsKey(username)) return false;
        User u = new User(username, password, name, 18, 170, 60, "Today");
        users.put(username, u);
        return true;
    }

    public User getCurrentUser() { return currentUser; }
    public Goal getCurrentGoal() { return currentGoal; }
    public DailyActivity getToday() { return today; }
    public List<DailyActivity> getHistory() { return history; }

    public double getGoalProgressPercent() {
        return currentGoal.getProgressPercent(startWeightKg, currentUser.getWeightKg());
    }

    public String getMotivationalMessage() {
        double pct = getGoalProgressPercent();
        if (pct >= 100) return "Goal reached! Great work!";
        if (pct >= 50) return "Halfway there, keep going!";
        return "Every step counts. You've got this!";
    }
    public String getTrainerAdvice() {
    StringBuilder advice = new StringBuilder();
    DailyActivity t = getToday();
    User u = getCurrentUser();

    if (t.getSteps() < t.getStepGoal()) {
        advice.append("• You're ").append(t.getStepGoal() - t.getSteps())
              .append(" steps short of your goal today. A short evening walk can close that gap.\n\n");
    } else {
        advice.append("• Great job hitting your step goal today!\n\n");
    }

    if (t.getWaterLiters() < t.getWaterGoalLiters()) {
        advice.append("• Water intake is below target. Try to drink ")
              .append(String.format("%.1f", t.getWaterGoalLiters() - t.getWaterLiters()))
              .append("L more before the day ends.\n\n");
    }

    if (t.getExerciseMinutes() < t.getExerciseGoalMinutes()) {
        advice.append("• You still have ").append(t.getExerciseGoalMinutes() - t.getExerciseMinutes())
              .append(" minutes of exercise left for today.\n\n");
    }

    String bmiStatus = u.getBmiStatus();
    if (bmiStatus.equals("Underweight")) {
        advice.append("• Your BMI suggests you're underweight. Consider increasing calorie intake with nutrient-dense foods.\n\n");
    } else if (bmiStatus.equals("Overweight") || bmiStatus.equals("Obese")) {
        advice.append("• Your BMI suggests focusing on a calorie deficit combined with regular cardio.\n\n");
    } else {
        advice.append("• Your BMI is in a healthy range — keep maintaining your current routine.\n\n");
    }

    double pct = getGoalProgressPercent();
    advice.append("• You are ").append(String.format("%.0f", pct)).append("% toward your goal. ");
    advice.append(pct < 50 ? "Stay consistent, results build over time." : "You're making solid progress, keep it up!");

    return advice.toString();
}
}