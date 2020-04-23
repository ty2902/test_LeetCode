import java.util.Stack;

public class zcy1_2 {
    //对栈进行逆序操作
    public static Integer getLastItem(Stack stack){
        int temp=(int)stack.pop();
        if(stack.size()==0){
            return temp;
        }else{

            int last=getLastItem(stack);
            stack.push(temp);
            return last;
        }
    }

    public static void reverse(Stack stack){
        if(stack.isEmpty()){
            return;
        }else{
            int temp=getLastItem(stack);
            reverse(stack);
            stack.push(temp);
        }

    }
    public static void main(String[] args) {

        Stack<Integer> stack=new Stack<>();
        stack.add(3);
        stack.add(5);
        stack.add(8);
        reverse(stack);
        System.out.println(stack);

    }
}
