import java.util.List;
import java.util.Stack;
//维护栈的最小元素
public class zcy1_1 {
    private Stack<Integer> stack=new Stack<>();
    private Stack<Integer>  minStack =new Stack<>();

    public Integer pop(){
       if(stack.peek()==minStack.peek()){
           minStack.pop();
       }

       return stack.pop();
    }

    public void  push(Integer item){
        stack.push(item);
        if(minStack.size()==0){
            minStack.push(item);
        }else{
            Integer temp=minStack.peek();
            if(temp>item){
                minStack.push(item);
            }
        }
    }

    public Integer  getmin(){
        return minStack.peek();

    }

    public static void main(String[] args) {
        zcy1_1 test =new zcy1_1();
        test.push((Integer)5);
        test.push((Integer)3);
        test.push((Integer)6);
        System.out.println(test.getmin());

    }
}
