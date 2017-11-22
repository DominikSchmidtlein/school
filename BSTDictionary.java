/* The challenge is to build a dictionary (hashmap) which uses a binary search tree
to store entries.
The following functions have an implementation:
search, insert, delete, print. */

public class BSTDictionary<E, K extends Sortable> implements Dictionary<E, K>
{
    BSTNode<E,K> root;
    public BSTDictionary(){
        
    }
    
    public E search(K key){
        return searchSubstring(key, root);
    }
    private E searchSubstring(K key, BSTNode<E, K> node){
    /*base case:if node is null, this branch does not contain the key -> return null
     * if the key of the current node matches the search key, return the element (desired node has
     * been found)
     */
        if(node == null){
            return null;
        }
        if(node.getKey().toString().equals(key.toString())){
            return node.getElement();
        }
    /*Search left subtree
     * search right subtree
     * if either search returns not null, the key was found, then return the not null object
     * otherwise, the key is not in this substring... return null
     */    
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
        /*consider special case: empty tree
           * then insert as first node and then return
             */    
        BSTNode<E,K> newNode = new BSTNode<E,K>(key, element, null, null);
        if(root == null){
            root = newNode;
            return;
        }
        BSTNode<E,K> curr = root;
        /*in each loop, compare key of new node and the current node. chose path left or right
           * if the choice, (left or right) is empty, insert the node
           * if another key matches this key, a duplicate key is trying to be added, therefore
           * discard and return
             */
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
        /*Take care of special cases: empty tree, root being deleted
           * if empty tree, nothing can be deleted therefore return without operating
           * if root is being deleted then there is no prev, allow prevnode to be null
           * then use findPrev to get the parent node of the dead node
             */
        BSTNode<E,K> prev = null;
        BSTNode<E,K> deadNode = null;
        if(root == null){
            return;
        }
        else if(root.getKey().compareTo(key) == 0){
            deadNode = root;
        }
        else{
            prev = findPrev(key, root);
            if(prev == null){ //if prev == null, there is no parent node because the key doesn't exist
                return;
            }
        }
        /*Find what side of prev (parent node) the dead node is on
           * 
             */
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
        BSTNode<E,K> replacementNode = findReplacement(deadNode);
        /*
         * the node that will be deleted is at deadNode
         * the node that points to deadNode is at prev
         * the only case where there is no prev node, delete root, is already taken care of
         * the node which will take deadNode's spot is at replacementNode
         */
        if(replacementNode != null){ //if replacement is null, the dead node was a leaf and needs not be replaced
            replacementNode.setLeft(deadNode.getLeft()); //give the newnode in the position the same
            replacementNode.setRight(deadNode.getRight());// children as the one before it
        }
        /*Set the correct child node of prev to the replacement node. if there is no replacement node,
           * replacement node will be null and therefore prev will then point to null
             */
        if(prev != null){
            if(onLeft){
                prev.setLeft(replacementNode);
            }
            else{
                prev.setRight(replacementNode);
            }
        }
    }
    private BSTNode<E,K> findPrev(K key, BSTNode<E,K> node){
        /*general case:
         * findprev for both subtrees
         * if either one is not null return it
         * null means the key was not found anywhere in the respective branch
         * if both are null return null because the key is nowhere in this tree
         * 
         */
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
        BSTNode<E,K> left = findPrev(key, node.getLeft());
        if(left != null){
            return left;
        }
        BSTNode<E,K> right = findPrev(key, node.getRight());
        if(right != null){
            return right;
        }
        return null;
    }
    private BSTNode<E,K> findReplacement(BSTNode<E,K> deadNode){
        /*
         * return null if no replacement is needed (leaf)
         * consider special cases:
         * left child node is null -> if right child node has left as null, then it is the most left child therefore return it
         * -> otherwise, where left child is null, find the leftmost node in the right child's tree and return it
         */
        if(deadNode.getLeft() == null && deadNode.getRight() == null){
            return null;
        }
        else if(deadNode.getLeft() == null){
            if(deadNode.getRight().getLeft() == null){ //getRight() is the leftmost node
                BSTNode<E,K> replacementNode = deadNode.getRight();// getRght becomes replacement
                deadNode.setRight(replacementNode.getRight()); //the replacement node's right child gets attached to the replacement node's parent (deadNode)
                return replacementNode;
            }
            else{
                BSTNode<E,K> prev = deadNode;
                BSTNode<E,K> replacementNode = deadNode.getRight();
                while(replacementNode.getLeft() != null){ //find leftmost node it right subtree
                    prev = replacementNode;
                    replacementNode = replacementNode.getLeft();
                }
                prev.setLeft(replacementNode.getRight()); //preserve the subtree on the right of the replacemetn node
                return replacementNode;
            }
        }
        else{
            if(deadNode.getLeft().getRight() == null){// same as above, return getleft and preserve its left subtree
                BSTNode<E,K> replacementNode = deadNode.getLeft();
                deadNode.setLeft(replacementNode.getLeft());
                return replacementNode;
            }
            else{
                BSTNode<E,K> prev = deadNode;
                BSTNode<E,K> replacementNode = deadNode.getLeft();
                while(replacementNode.getRight() != null){
                    prev = replacementNode;
                    replacementNode = replacementNode.getRight();
                }
                prev.setRight(replacementNode.getRight());// preserve the replacement node's not empty child
                return replacementNode;
            }
        }        
    }

    
    public void printTree(){
        printSubtree(root);
    }
    private void printSubtree(BSTNode<E,K> node){
        /*
           * in order to print in order:
           * print lef subtree
           * print middle node
           * print right subtree
           * 
           * if node == null, there is nothing to print, therefore return
           * all nodes are printed when they are the middle node
             */
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
    private int depthSubtree(BSTNode<E,K> node){
        /*general rule:
           * calculate the depth of left subtree + 1 (for current node)
           * calculate the depth of the right subtree + 1
           * return the larger one since the max depth of the overall tree is being calculated
             */
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
}
