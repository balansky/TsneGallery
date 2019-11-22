#ifndef TSNE_TREE_H
#define TSNE_TREE_H

#include <vector>
#include <limits>
#include <queue>
#include <algorithm>

namespace tsne{

template<typename T>
struct Distance{
    int dim;
    
    Distance(): dim(0){}
    explicit Distance(int dim):dim(dim){}
    virtual ~Distance() = default;

    virtual T get(const T* t1, const T* t2) const = 0;
    

};

template<typename T>
struct EuclideanDistance:Distance<T> {

    EuclideanDistance(){}
    explicit EuclideanDistance(int dim): Distance<T>(dim){}
    ~EuclideanDistance() = default;

    T get(const T* t1, const T* t2) const{
            T dd = .0;
            for(int d = 0; d < Distance<T>::dim; d++){
                T t = (t1[d] - t2[d]);
                dd += t * t;
            }
            return sqrt(dd);
    }

};


template<typename T>
class VpTree{

private:

    int dim;

protected:

    Distance<T> *_distance;

    struct DataPoint{

        size_t index;
        const T *x;
        DataPoint():index(0), x(nullptr){}

        DataPoint(size_t ind, const T *x): index(ind), x(x){}

        DataPoint(const DataPoint &other){
            if(this != &other){
                index = other.index;
                x = other.x;
            }
        }

        DataPoint& operator=(const DataPoint &other){
            if(this != &other){
                index = other.index;
                x = other.x;
            }
            return *this;
        }


    } *_pts;

    struct Node{
        T radius;
        DataPoint *p;
        Node *left;
        Node *right;

        Node(): radius(0), p(nullptr), left(nullptr),right(nullptr){}

        Node(T r, DataPoint *p): radius(r), p(p), left(nullptr),right(nullptr){}

        ~Node(){
            delete left;
            delete right;
        }
    } *_root;

    struct HeapItem {
        HeapItem( size_t index, T dist) :
            index(index), dist(dist) {}
        size_t index;
        T dist;
        bool operator<(const HeapItem& o) const {
            return dist < o.dist;
        }
    };

    struct DistanceComparator
    {
        const DataPoint& item;
        Distance<T> *dist;
        explicit DistanceComparator(const DataPoint& item, Distance<T> *dist) : item(item), dist(dist) {}
        bool operator()(const DataPoint& a, const DataPoint& b) {
            return dist->get(item.x, a.x) < dist->get(item.x, b.x);
        }
    };

    Node* insert(DataPoint *items, size_t lower, size_t upper){
        if (upper == lower) return nullptr;

        Node *node = new Node();
        node->p = items + lower;

        if(upper - lower > 1){
            int i = (int) ((double)rand() / RAND_MAX * (upper - lower - 1)) + lower;
            std::swap(items[lower], items[i]);

            // Partition around the median distance
            int median = (upper + lower) / 2;
            std::nth_element(items + lower + 1,
                             items + median,
                             items + upper,
                             DistanceComparator(items[lower], _distance));

            // Threshold of the new node will be the distance to the median
            node->p = items + lower;
            node->radius = (_distance->get(items[lower].x, items[median].x));

            // Recursively build tree
            node->left = insert(items, lower + 1, median);
            node->right = insert(items, median, upper);

        }
        return node;
    }

    void search(Node *node, const T *target, unsigned int k, std::priority_queue<HeapItem>& heap, T& tau)
    {
        if (node == NULL) return;    // indicates that we're done here

        // Compute distance between target and current node
        T dist = _distance->get(node->p->x, target);

        // If current node within radius tau
        if (dist < tau) {
            if (heap.size() == k) heap.pop();                // remove furthest node from result list (if we already have k results)
            heap.push(HeapItem(node->p->index, dist));           // add current node to result list
            if (heap.size() == k) tau = heap.top().dist;    // update value of tau (farthest point in result list)
        }

        // Return if we arrived at a leaf
        if (node->left == NULL && node->right == NULL) {
            return;
        }

        // if node is laied inside or intersect with the search radius
        if(dist <= tau || dist - node->radius <= tau){
            search(node->left, target, k, heap, tau);
            search(node->right, target, k, heap, tau);
        }
        // if node is laied outside of the search radius
        else if(dist - node->radius > tau){
            search(node->right, target, k, heap, tau);
        }
    }

public:

    VpTree(): dim(0), _distance(nullptr), _pts(nullptr),  _root(nullptr){}

    explicit VpTree(int dim): dim(dim), _distance(nullptr), _pts(nullptr), _root(nullptr) {
        _distance = new EuclideanDistance<T>(dim);
    }

    VpTree(size_t n, int dim, const T *x):dim(dim), _root(nullptr), _pts(nullptr){
        _distance = new EuclideanDistance<T>(dim);
        build(n, x);
    }

    ~VpTree(){
        delete _root;
        delete _pts;
        delete _distance;
    }


    void search(const T *target, int k, std::vector<size_t> &results, std::vector<T> &distances){

        std::priority_queue<HeapItem> heap;

        // Variable that tracks the distance to the farthest point in our results
        T tau = std::numeric_limits<T>::max();

        // Perform the searcg
        search(_root, target, k, heap, tau);

        // Gather final results
        results.clear(); distances.clear();
        while (!heap.empty()) {
            results.push_back(heap.top().index);
            distances.push_back(heap.top().dist);
            heap.pop();
        }

        // Results are in reverse order
        std::reverse(results.begin(), results.end());
        std::reverse(distances.begin(), distances.end());

    }

    void build(size_t n, const T *x){
        if(_pts){
            delete _root;
            delete _pts;
            _root = nullptr;
        }
        _pts = new DataPoint[n];
        for(size_t i = 0; i < n; i++){
            _pts[i] = DataPoint(i, x + i*dim);
        }
        _root = insert(_pts, 0, n);
    }

};


template <typename T>
class BarnesHutTree{

    protected:
    int n;
    int n_dims;
    T *data;
    struct Cell {
        int size;
        int cum_size;
        bool is_leaf;
        T *center;
        T *width;
        T *center_of_mass;
	    std::vector<Cell*> children;
        Cell():size(0),cum_size(0),is_leaf(true), center(nullptr),width(nullptr),center_of_mass(nullptr){};
    } *_root;

    void insert(int idx, Cell *cell, T *d){

    }

    public:
    BarnesHutTree() = default;
    BarnesHutTree(int n, int n_dims, T *data):n(n), n_dims(n_dims), data(data){


        _root = new Cell();
        for(int i = 0; i < n; i++){
            insert(i, _root, data + i * n_dims);
        }
    };


    void insert(T *d){
        insert(_root, d);
    };

};

}


#endif