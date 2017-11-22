/* Challenge: build a dictionary which uses an AVL tree to store entries.
AVL tree is a tree which is self balancing, which reduces time for regular
operations. https://en.wikipedia.org/wiki/AVL_tree */

import java.util.List;
import java.util.ArrayList;
public class AVLDictionary<E, K extends Sortable> implements Dictionary<E, K>
{
    /* See BSTDictionary for comments other than on the balance method
     */
    AVLNode<E,K> root;
    public AVLDictionary(){
        
    }
    
    public E search(K key){
        return searchSubstring(key, root);
    }
    private E searchSubstring(K key, AVLNode<E, K> node){
        if(node == null){
            return null;
        }
        if(node.getKey().toString().equals(key.toString())){
            return node.getElement();
        }
        E left = searchSubstring(key, node.getLeft());
        if(left != null){
            return left;
        }
        E right = searchSubstring(key, node.getRight());
        if(right != null){
            return right;
        }
        return null;
    }
    
    public void insert(K key, E element){
        insertStart(key, element);
        balanceTree();
    }
    private void insertStart(K key, E element){
        AVLNode<E,K> newNode = new AVLNode<E,K>(key, element, null, null,0);
        if(root == null){
            root = newNode;
            return;
        }
        AVLNode<E,K> curr = root;
        while(true){
            K k1 = newNode.getKey();
            K k2 = curr.getKey();
            if(k1.compareTo(k2) < 0){
                if(curr.getLeft() == null){
                    curr.setLeft(newNode);
                    return;
                }
                curr = curr.getLeft();
            }
            else if(k1.compareTo(k2) > 0){
                if(curr.getRight() == null){
                    curr.setRight(newNode);
                    return;
                }
                curr = curr.getRight();
            }
            else if(k1.compareTo(k2) == 0){
                return;
            }
        }
    }
    
    public void delete(K key){
        AVLNode<E,K> prev = null;
        AVLNode<E,K> deadNode = null;
        if(root == null){
            return;
        }
        else if(root.getKey().compareTo(key) == 0){
            deadNode = root;
        }
        else{
            prev = findPrev(key, root);
            if(prev == null){
                return;
            }
        }
        boolean onLeft = false;
        if(prev != null){
            if(prev.getLeft() != null){
                 if(prev.getLeft().getKey().compareTo(key) == 0){
                     deadNode = prev.getLeft();
                     onLeft = true;
                 }
            }
            if(prev.getRight() != null){
                if(prev.getRight().getKey().compareTo(key) == 0){
                    deadNode = prev.getRight();
                    onLeft = false;
                }
            }
        }
        AVLNode<E,K> replacementNode = findReplacement(deadNode);
        /*
         * the node that will be deleted is at deadNode
         * the node that points to deadNode is at prev
         * the only case where there is no prev node, delete root, is already taken care of
         * the node which will take deadNode's spot is at replacementNode
         */
        if(replacementNode != null){
            replacementNode.setLeft(deadNode.getLeft());
            replacementNode.setRight(deadNode.getRight());
        }
        if(prev != null){
            if(onLeft){
                prev.setLeft(replacementNode);
            }
            else{
                prev.setRight(replacementNode);
            }
        }
        balanceTree();
    }
    private AVLNode<E,K> findPrev(K key, AVLNode<E,K> node){
        if(node == null){
            return null;
        }
        if(node.getLeft() != null){
            if(node.getLeft().getKey().toString().equals(key.toString())){
                return node;
            }
        }
        if(node.getRight() != null){
            if(node.getRight().getKey().toString().equals(key.toString())){
                return node;
            }
        }
        AVLNode<E,K> left = findPrev(key, node.getLeft());
        if(left != null){
            return left;
        }
        AVLNode<E,K> right = findPrev(key, node.getRight());
        if(right != null){
            return right;
        }
        return null;
    }
    private AVLNode<E,K> findReplacement(AVLNode<E,K> deadNode){
        /*
         * return null if no replacement is needed (leaf)
         * otherwise return the most central node on the left subtree
         * if left subtree is empty, return top node on right subtree
         * 
         * 
         */
        if(deadNode.getLeft() == null && deadNode.getRight() == null){
            return null;
        }
        else if(deadNode.getLeft() == null){
            if(deadNode.getRight().getLeft() == null){
                AVLNode<E,K> replacementNode = deadNode.getRight();
                deadNode.setRight(replacementNode.getRight());
                return replacementNode;
            }
            else{
                AVLNode<E,K> prev = deadNode;
                AVLNode<E,K> replacementNode = deadNode.getRight();
                while(replacementNode.getLeft() != null){
                    prev = replacementNode;
                    replacementNode = replacementNode.getLeft();
                }
                prev.setLeft(replacementNode.getRight());
                return replacementNode;
            }
        }
        else{
            if(deadNode.getLeft().getRight() == null){
                AVLNode<E,K> replacementNode = deadNode.getLeft();
                deadNode.setLeft(replacementNode.getLeft());
                return replacementNode;
            }
            else{
                AVLNode<E,K> prev = deadNode;
                AVLNode<E,K> replacementNode = deadNode.getLeft();
                while(replacementNode.getRight() != null){
                    prev = replacementNode;
                    replacementNode = replacementNode.getRight();
                }
                prev.setRight(replacementNode.getRight());
                return replacementNode;
            }
        }        
    }

    
    public void printTree(){
        printSubtree(root);
    }
    private void printSubtree(AVLNode<E,K> node){
        if(node == null){
            return;
        }
        
        printSubtree(node.getLeft());
        System.out.println("Key: " + node.getKey().toString() + " Element: " + node.getElement());
        printSubtree(node.getRight());
        return;
    }
    
    public int depth(){
        return depthSubtree(root);
    }
    private int depthSubtree(AVLNode<E,K> node){
        if(node == null){
            return 0;
        }

        int left = depthSubtree(node.getLeft()) + 1;
        int right = depthSubtree(node.getRight()) + 1;
        if(left > right){
            return left;
        }
        else{
            return right;
        }
    }
    
    public void balanceTree(){
        List<AVLNode<E,K>> nodes = listNodesInOrder(root);// get list of nodes in order
        root = constructTree(nodes);// construct tree from ordered list and set root to top
    }
    private List<AVLNode<E,K>> listNodesInOrder(AVLNode<E,K> node){
        /* general: add all from left subtree, add node, add all from right subtree, return the list
         * base: if a node is null return an empty list because nothing should be added to the ongoing list
         * 
         */
        if(node == null){
            return new ArrayList<AVLNode<E,K>>();
        }
        List<AVLNode<E,K>> list = new ArrayList<AVLNode<E,K>>();
        list.addAll(listNodesInOrder(node.getLeft()));
        list.add(node);
        list.addAll(listNodesInOrder(node.getRight()));
        return list;
    }
    private AVLNode<E,K> constructTree(List<AVLNode<E,K>> list){
        /*general: make reference to middle node from list
         *      attach a balanced tree to left child, made from left half of list
         *      attach a balanced tree to the right child, constructed from the right half of the list
         *      return the original node
         *      
         * base: if the size of the list is zero, there is no more tree's to consturct, that child should be null
         * therefore return null
         * 
         * hint: do not include the middle node in either sublist
         */
        if(list.size() == 0){
            return null;
        }
        int halfWay = (list.size() - 1)/2;
        int right = halfWay + 1;
        if(right > list.size()) right = list.size(); // if out of bounds, fix to max possible value
        AVLNode<E,K> node = list.get(halfWay);
        node.setLeft(constructTree(list.subList(0,halfWay))); //subList() is inclusive for first index and
        node.setRight(constructTree(list.subList(right,list.size())));// exclusive for second index
        return node;
    }
}
