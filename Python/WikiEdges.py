import pprint
import operator
# new_file = open("./overview_communities.txt", "w")


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

    print("Id = {}".format(max(degree_dict.items(), key=operator.itemgetter(1))[0]))
    list_of_degrees = list(degree_dict.values())
    current_d = length_streak = mode = longest_streak = 0
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
    print_out_communities("output_communities.txt", minimum=5)
