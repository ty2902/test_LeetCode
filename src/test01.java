import java.util.*;

public class test01 {

    public List<Integer> findDisappearedNumbers(int[] nums) {
         List<Integer> result=new ArrayList<>();
         if(nums.length==0){
             return result;
         }
        for (int i = 0 ; i < nums.length ; i++) {
            if(nums[i]==i+1 || nums[i]==-1){
                continue;
            }else {
                int index=nums[i]-1;
                if(nums[i]==nums[index]){
                    nums[i]=-1;
                    continue;

                }else{
                    int temp;
                    temp=nums[i];
                    nums[i]=nums[index];
                    nums[index]=temp;
                    i=i-1;
                }
            }

        }
        for (int i = 0; i <nums.length ; i++) {
            if(nums[i]==-1){
                result.add(i+1);
            }

        }
        return result;
    }
    public static void main(String[] args) {
        int[] test ={};

    }


}