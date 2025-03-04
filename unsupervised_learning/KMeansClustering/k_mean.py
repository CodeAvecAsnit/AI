import numpy as np

class KMeans:
    def __init__(self, k=2, max_iterations=500):
        self.k = k
        self.max_iterations = max_iterations
        self.clusters = {} 

    def _init_random_centroids(self, x):
        """ Initialize k random centroids from the dataset x """
        np.random.seed(23)
        random_indices = np.random.choice(x.shape[0], self.k, replace=False)
        self.clusters = {i: {'center': x[idx], 'points': []} for i, idx in enumerate(random_indices)}

    def assign_clusters(self, x):
        """ Assign each point in x to the nearest centroid """
        for i in range(self.k):
            self.clusters[i]['points'] = []  

        for idx in range(x.shape[0]):
            distances = [np.linalg.norm(x[idx] - self.clusters[i]['center']) for i in range(self.k)]
            cluster_idx = np.argmin(distances)
            self.clusters[cluster_idx]['points'].append(x[idx])

    def update_clusters(self):
        """ Update the centroids by computing the mean of assigned points """
        for i in range(self.k):
            points = np.array(self.clusters[i]['points'])
            if points.shape[0] > 0:
                self.clusters[i]['center'] = points.mean(axis=0)

    def predict_cluster(self, x):
        """ Predict which cluster a new data point belongs to """
        predictions = []
        for i in range(x.shape[0]):
            distances = [np.linalg.norm(x[i] - self.clusters[j]['center']) for j in self.clusters]
            predictions.append(np.argmin(distances))
        return np.array(predictions)