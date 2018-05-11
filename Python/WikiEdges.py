import operator
import matplotlib.pyplot as plt


def plot_degree_distribution(input_file):
    xs = []
    ys = []
    with open(input_file) as infile:
        for line in infile:
            values = line.split(sep="\t")
            xs.append(int(values[0]))
            ys.append(int(values[1]))
    ax = plt.subplot(111)
    ax.set_yscale("log")
    ax.set_ylim(ymin=1, ymax=1000000)
    ax.set_xlim(xmin=0, xmax=1000)
    plt.semilogy(xs[:1000], ys[:1000])
    plt.title('Degree distribution')
    plt.grid(True)
    plt.show()


def plot_incoming_outgoing(input_file):
    incoming_dict = {}
    outgoing_dict = {}
    dicts = [outgoing_dict, incoming_dict]
    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep=",")
            for i, v in enumerate(values):
                v = int(v)
                if v not in dicts[i]:
                    dicts[i][v] = 1
                else:
                    dicts[i][v] += 1

    print("Id = {}".format(max(outgoing_dict.items(), key=operator.itemgetter(1))))
    print("Id = {}".format(max(incoming_dict.items(), key=operator.itemgetter(1))))
    key_list = incoming_dict.keys()
    tuple_list = []
    for key in sorted(key_list):
        if key in outgoing_dict.keys():
            tuple_list.append((outgoing_dict[key], incoming_dict[key]))

    x, y = zip(*tuple_list)
    ax = plt.subplot(111)
    ax.set_yscale("log")
    ax.set_xscale("log")
    ax.set_ylim(ymin=1, ymax=1000000)
    ax.set_xlim(xmin=1, xmax=1000000)
    plt.scatter(x, y)
    plt.show()


def print_out_communities(input_file, minimum):
    total_dict = {}
    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep="\t")
            total_dict.setdefault(int(values[1]), []).append(int(values[0]))

    with open("./overview_communities.txt", "w") as f:
        for key, value in total_dict.items():
            f.write('%s\t%s\n' % (key, value))

    with open("./overview_communities_filtered.txt", "w") as f:
        for key, value in total_dict.items():
            if len(value) >= minimum:
                f.write('%s\t%s\n' % (key, value))


def csv_to_txt(input_file):
    output_file = "{}.txt".format(input_file[:-4])
    outfile = open(output_file, 'w')
    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep=",")
            outfile.write("{}\t{}\n".format(int(values[0]), int(values[1])))
    return output_file


def compute_degree_mode(input_file):
    degree_dict = {}

    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep="\t")
            for v in values:
                v = int(v)
                if v not in degree_dict:
                    degree_dict[v] = 1
                else:
                    degree_dict[v] += 1

    print("Id = {}".format(max(degree_dict.items(), key=operator.itemgetter(1))))
    list_of_degrees = list(degree_dict.values())
    current_d = 0
    length_streak = 0
    mode = 0
    longest_streak = 0
    with open("./degree_distribution.txt", 'w') as outfile:
        outfile.write("Links\tFreq\n")
        for d in sorted(list_of_degrees):
            if current_d == d:
                length_streak += 1
            else:
                if length_streak > longest_streak:
                    longest_streak = length_streak
                    mode = current_d
                outfile.write("{}\t{}\n".format(current_d, length_streak))
                current_d = d
                length_streak = 1

    return mode


def find_max_node_id(input_file):
    tmp = 0
    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep=",")
            tmp = max(tmp, int(values[0]), int(values[1]))
    return tmp


def new_mapping(input_file, output_file):
    new_set = set()
    with open(input_file, 'r') as infile:
        for line in infile:
            values = line.split(sep="\t")
            for v in values:
                new_set.add(int(v))

    with open(output_file, 'w') as outfile:
        for i, v in enumerate(sorted(new_set)):
            outfile.write("{}\t{}\n".format(i, v))


if __name__ == "__main__":
    # txt_file = csv_to_txt("./page_links.csv")
    # compute_degree_mode("./page_links.txt")
    # print(find_max_node_id("./page_links.csv"))
    # for i in [1, 3, 8, 17]:
    # print_out_communities("output_communities.txt", minimum=5)
    # plot_degree_distribution("degree_distribution.txt")
    plot_incoming_outgoing("page_links.csv")
