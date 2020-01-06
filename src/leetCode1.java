import java.util.HashMap;
import java.util.Map;
//leetCode01两数之和
public class leetCode1 {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            //值，索引
            map.put(nums[i], i);
        }
        throw new IllegalArgumentException("No two sum solution");
    }


    public static void main(String[] args) {
        int[] num={2,7,11,15};

        int target=9;
        leetCode1 test=new leetCode1();
        int[] result=test.twoSum(num,target);
        for (int i:result) {
            System.out.println(i+" ");

        }
    }


}
