
class Tree{
	
	Node root;
	class Node{
		int freq;
		Object value;
		Node l,r;
		int left_child_count, right_child_count;
		int size_of_node;
		
		public Node(int freq, String value) {
			// TODO Auto-generated constructor stub
			this.freq = freq;
			this.value = value;
			this.l = this.r = null;
			this.left_child_count = this.right_child_count = 0;
			this.size_of_node = 1;
		}
	}
	Tree()
	{
		this.root = null;
	}
	Tree(Node node){
		this.root = node;
	}
	
	void insert(int freq, String value){
		
		root = insert(freq, value, root);
	}
	Node insert(int freq, String value, Node node){
		if(node == null){
			node = new Node(freq, value);
		}else{
			if(freq < node.freq){
				node.left_child_count++;
				node.l = insert(freq, value, node.l);
			}else if(freq > node.freq){
				node.right_child_count++;
				node.r = insert(freq, value, node.r);
			}else if(freq == node.freq){
				if(node.size_of_node == 1){
					//List<String> list = new 
				}
			}
		}
		return node;
	}
}