import java.util.ArrayDeque;
import java.util.LinkedList;

public class zcy1_3 {
    public static void getMaxWindow(int[] arr,int window){
        if(arr==null  || window<1 || arr.length< window)
            return;
        int[] result=new int[arr.length-window+1];
        LinkedList<Integer> qmax=new LinkedList<Integer>();

        int index=0;

        for (int i = 0; i <arr.length ; i++) {
            while(!qmax.isEmpty() && arr[qmax.peekLast()] <= arr[i]) {
                qmax.pollLast();
            }
            qmax.addLast(i);
            if(qmax.peekFirst()==i-window){
                qmax.pollFirst();
            }
            if(i >= window-1){
                result[index++]=arr[qmax.peekFirst()];
            }
        }


        for (int i = 0; i < result.length; i++) {
            System.out.print(result[i]+" ");
        }




    }
    public static void main(String[] args) {
          int[] arr={4,3,5,4,3,3,6,7};
          getMaxWindow(arr,3);
    }
}
