Exp. No: 1 - Study of Operating System Environment (Linux/Windows commands)

COMMANDS (WINDOWS)
----------------------------------------
# Ex.No:1  Windows commands (type these in Command Prompt, one by one)

dir
md oslab
echo Hello OS Lab > oslab\file.txt
type oslab\file.txt
copy oslab\file.txt oslab\copy.txt
attrib +r oslab\file.txt
attrib oslab\file.txt
tasklist
attrib -r oslab\file.txt
del oslab\file.txt oslab\copy.txt
dir oslab

OUTPUT (LINUX)
----------------------------------------
$ uname -a
Linux vm 6.18.44-fc-v180 #1 SMP PREEMPT_DYNAMIC @0 x86_64 x86_64 x86_64 GNU/Linux
$ whoami
root
$ pwd
/tmp/w1
$ date
Wed Oct 28 01:16:51 UTC 2026
$ mkdir oslab
$ ls -l
total 4
drwxr-xr-x 2 root root 4096 Oct 28 01:16 oslab
$ echo "Hello OS Lab" > oslab/file.txt
$ cat oslab/file.txt
Hello OS Lab
$ cp oslab/file.txt oslab/copy.txt
$ wc -l oslab/file.txt
1 oslab/file.txt
$ chmod 600 oslab/file.txt
$ ls -l oslab
total 8
-rw-r--r-- 1 root root 13 Oct 28 01:16 copy.txt
-rw------- 1 root root 13 Oct 28 01:16 file.txt
$ df -h
Filesystem                                                                         Size  Used Avail Use% Mounted on
tmpfs                                                                              4.0G  308K  4.0G   1% /dev/shm
/dev/vda                                                                           252G  8.6G   10G  47% /
model_tools_unavailable                                                            4.0K     0  4.0K   0% /mnt/sandboxing/model_tools_env/v1/python
tmpfs                                                                              2.0G     0  2.0G   0% /sys/fs/cgroup
tmpfs                                                                              1.0M     0  1.0M   0% /run/sandbox-telemetry
rclone-filestore:claude_chat_01SCuKLtBrnqzCwakmGfsRDZ:/mnt/user-data/tool_results  1.0P     0  1.0P   0% /mnt/user-data/tool_results
rclone-filestore:claude_chat_01SCuKLtBrnqzCwakmGfsRDZ:/mnt/user-data/outputs       1.0P     0  1.0P   0% /mnt/user-data/outputs
rclone-filestore:claude_chat_01SCuKLtBrnqzCwakmGfsRDZ:/mnt/user-data/uploads       1.0P     0  1.0P   0% /mnt/user-data/uploads
rclone-filestore:claude_chat_01SCuKLtBrnqzCwakmGfsRDZ:/mnt/transcripts             1.0P     0  1.0P   0% /mnt/transcripts
$ free -h
               total        used        free      shared  buff/cache   available
Mem:           3.9Gi       196Mi       3.8Gi        15Mi        66Mi       3.7Gi
Swap:             0B          0B          0B
$ ps -ef | head -6
UID        PID  PPID  C STIME TTY          TIME CMD
root         1     0  1 01:15 ?        00:00:00 /process_api --firecracker-init --addr 0.0.0.0:2024 --max-ws-buffer-size 32768 --block-local-connections --control-vsock-port 5001 --listen-vsock-port 2024 --log-vsock-port 5002
root         2     0  0 01:15 ?        00:00:00 [kthreadd]
root         3     2  0 01:15 ?        00:00:00 [pool_workqueue_release]
root         4     2  0 01:15 ?        00:00:00 [kworker/R-rcu_gp]
root         5     2  0 01:15 ?        00:00:00 [kworker/R-sync_wq]
$ top -b -n 1 | head -12
top - 01:16:52 up 1 min,  0 user,  load average: 0.16, 0.07, 0.02
Tasks:  54 total,   1 running,  53 sleeping,   0 stopped,   0 zombie
%Cpu(s):  0.0 us,  0.0 sy,  0.0 ni,100.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st 
MiB Mem :   3995.8 total,   3882.8 free,    198.8 used,     67.3 buff/cache     
MiB Swap:      0.0 total,      0.0 free,      0.0 used.   3797.0 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND
    1 root      20   0   19540   6220   5812 S   0.0   0.2   0:00.88 process_a+
    2 root      20   0       0      0      0 S   0.0   0.0   0:00.01 kthreadd
    3 root      20   0       0      0      0 S   0.0   0.0   0:00.00 pool_work+
    4 root       0 -20       0      0      0 I   0.0   0.0   0:00.00 kworker/R+
    5 root       0 -20       0      0      0 I   0.0   0.0   0:00.00 kworker/R+
$ rm oslab/file.txt oslab/copy.txt
$ ls -l oslab
total 0





Exp. No: 2 - CPU Scheduling Algorithms: FCFS, SJF, Priority, Round Robin
========================================

PROGRAM (Ex2_CPU_Scheduling.c)
----------------------------------------
/* Ex.No:2 CPU Scheduling: FCFS, SJF (non-preemptive), Priority, Round Robin */
#include <stdio.h>

typedef struct { int pid, at, bt, pr, rem, ct, tat, wt, done; } Process;
Process p[20];
int n;

void reset() {
    for (int i = 0; i < n; i++) { p[i].rem = p[i].bt; p[i].done = 0; }
}

void display(const char *name) {
    float tw = 0, tt = 0;
    printf("\n--- %s ---\n", name);
    printf("PID\tAT\tBT\tPR\tCT\tTAT\tWT\n");
    for (int i = 0; i < n; i++) {
        p[i].tat = p[i].ct - p[i].at;      /* Turnaround = Completion - Arrival */
        p[i].wt  = p[i].tat - p[i].bt;     /* Waiting = Turnaround - Burst */
        tw += p[i].wt; tt += p[i].tat;
        printf("P%d\t%d\t%d\t%d\t%d\t%d\t%d\n", p[i].pid, p[i].at, p[i].bt,
               p[i].pr, p[i].ct, p[i].tat, p[i].wt);
    }
    printf("Average Waiting Time    = %.2f\n", tw / n);
    printf("Average Turnaround Time = %.2f\n", tt / n);
}

/* mode 0 = FCFS, 1 = SJF, 2 = Priority (all non-preemptive) */
void nonPreemptive(int mode, const char *name) {
    int time = 0, count = 0;
    reset();
    while (count < n) {
        int sel = -1, best = 0;
        for (int i = 0; i < n; i++) {
            if (!p[i].done && p[i].at <= time) {
                int key = (mode == 0) ? p[i].at : (mode == 1) ? p[i].bt : p[i].pr;
                if (sel == -1 || key < best) { sel = i; best = key; }
            }
        }
        if (sel == -1) {                       /* CPU idle: jump to next arrival */
            int next = -1;
            for (int i = 0; i < n; i++)
                if (!p[i].done && (next == -1 || p[i].at < next)) next = p[i].at;
            time = next;
            continue;
        }
        time += p[sel].bt;
        p[sel].ct = time;
        p[sel].done = 1;
        count++;
    }
    display(name);
}

void roundRobin() {
    int tq, time = 0, count = 0, front = 0, rear = 0;
    int queue[1000], admitted[20] = {0};
    printf("Enter time quantum: ");
    scanf("%d", &tq);
    reset();

    time = p[0].at;
    for (int i = 0; i < n; i++) if (p[i].at < time) time = p[i].at;

    while (count < n) {
        for (int i = 0; i < n; i++)            /* admit newly arrived processes */
            if (!admitted[i] && p[i].at <= time) { queue[rear++] = i; admitted[i] = 1; }
        if (front == rear) {                   /* queue empty: advance time */
            time++;
            continue;
        }
        int i = queue[front++];
        int run = (p[i].rem < tq) ? p[i].rem : tq;
        time += run;
        p[i].rem -= run;
        for (int j = 0; j < n; j++)            /* admit processes arrived meanwhile */
            if (!admitted[j] && p[j].at <= time) { queue[rear++] = j; admitted[j] = 1; }
        if (p[i].rem > 0) queue[rear++] = i;
        else { p[i].ct = time; p[i].done = 1; count++; }
    }
    display("Round Robin");
}

int main() {
    int ch;
    printf("Enter number of processes: ");
    scanf("%d", &n);
    for (int i = 0; i < n; i++) {
        p[i].pid = i + 1;
        printf("P%d - Arrival time, Burst time, Priority: ", i + 1);
        scanf("%d %d %d", &p[i].at, &p[i].bt, &p[i].pr);
    }
    do {
        printf("\n1.FCFS  2.SJF  3.Priority  4.Round Robin  5.Exit\nChoice: ");
        scanf("%d", &ch);
        switch (ch) {
            case 1: nonPreemptive(0, "FCFS"); break;
            case 2: nonPreemptive(1, "SJF (Non-Preemptive)"); break;
            case 3: nonPreemptive(2, "Priority (Non-Preemptive)"); break;
            case 4: roundRobin(); break;
            case 5: break;
            default: printf("Invalid choice\n");
        }
    } while (ch != 5);
    return 0;
}

SAMPLE INPUT
----------------------------------------
4
0 5 2
1 3 1
2 8 4
3 6 3
1
2
3
4
2
5

SAMPLE OUTPUT
----------------------------------------
Enter number of processes: 4
P1 - Arrival time, Burst time, Priority: 0 5 2
P2 - Arrival time, Burst time, Priority: 1 3 1
P3 - Arrival time, Burst time, Priority: 2 8 4
P4 - Arrival time, Burst time, Priority: 3 6 3

1.FCFS  2.SJF  3.Priority  4.Round Robin  5.Exit
Choice: 1

--- FCFS ---
PID	AT	BT	PR	CT	TAT	WT
P1	0	5	2	5	5	0
P2	1	3	1	8	7	4
P3	2	8	4	16	14	6
P4	3	6	3	22	19	13
Average Waiting Time    = 5.75
Average Turnaround Time = 11.25

1.FCFS  2.SJF  3.Priority  4.Round Robin  5.Exit
Choice: 2

--- SJF (Non-Preemptive) ---
PID	AT	BT	PR	CT	TAT	WT
P1	0	5	2	5	5	0
P2	1	3	1	8	7	4
P3	2	8	4	22	20	12
P4	3	6	3	14	11	5
Average Waiting Time    = 5.25
Average Turnaround Time = 10.75

1.FCFS  2.SJF  3.Priority  4.Round Robin  5.Exit
Choice: 3

--- Priority (Non-Preemptive) ---
PID	AT	BT	PR	CT	TAT	WT
P1	0	5	2	5	5	0
P2	1	3	1	8	7	4
P3	2	8	4	22	20	12
P4	3	6	3	14	11	5
Average Waiting Time    = 5.25
Average Turnaround Time = 10.75

1.FCFS  2.SJF  3.Priority  4.Round Robin  5.Exit
Choice: 4
Enter time quantum: 2

--- Round Robin ---
PID	AT	BT	PR	CT	TAT	WT
P1	0	5	2	14	14	9
P2	1	3	1	11	10	7
P3	2	8	4	22	20	12
P4	3	6	3	20	17	11
Average Waiting Time    = 9.75
Average Turnaround Time = 15.25

1.FCFS  2.SJF  3.Priority  4.Round Robin  5.Exit
Choice: 5




Exp. No: 3 - Process Management: fork, exec, wait, exit
========================================

PROGRAM (Ex3_Process_Management.c)
----------------------------------------
/* Ex.No:3 Process Management: fork(), exec(), wait(), exit() */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main() {
    pid_t pid1, pid2;
    int status;

    setbuf(stdout, NULL);                          /* unbuffered output */
    printf("Parent process PID = %d\n", getpid());

    pid1 = fork();                                 /* first child */
    if (pid1 < 0) { perror("fork"); exit(1); }
    if (pid1 == 0) {
        printf("Child 1: PID = %d, Parent PID = %d\n", getpid(), getppid());
        for (int i = 1; i <= 3; i++) {
            printf("Child 1: iteration %d\n", i);
            sleep(1);                              /* CPU switches to another process */
        }
        exit(10);                                  /* exit status */
    }

    pid2 = fork();                                 /* second child */
    if (pid2 < 0) { perror("fork"); exit(1); }
    if (pid2 == 0) {
        printf("Child 2: PID = %d, replacing image with 'ls -l'\n", getpid());
        execlp("ls", "ls", "-l", (char *)NULL);
        perror("execlp");                          /* only runs if exec fails */
        exit(1);
    }

    /* parent waits once for every child */
    for (int i = 0; i < 2; i++) {
        pid_t done = wait(&status);
        if (WIFEXITED(status))
            printf("Parent: child PID %d terminated, exit status = %d\n",
                   done, WEXITSTATUS(status));
    }
    return 0;
}

SAMPLE INPUT
----------------------------------------
(no input needed)

SAMPLE OUTPUT
----------------------------------------
Parent process PID = 161
Child 1: PID = 162, Parent PID = 161
Child 1: iteration 1
Child 2: PID = 163, replacing image with 'ls -l'
total 0
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:15 Ex1
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:16 Ex10
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:15 Ex2
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:16 Ex3
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:15 Ex4
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:15 Ex5
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:15 Ex6
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:16 Ex7
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:16 Ex8
drwxr-xr-x 1 claude ubuntu 0 Oct 28 01:16 Ex9
Parent: child PID 163 terminated, exit status = 0
Child 1: iteration 2
Child 1: iteration 3
Parent: child PID 162 terminated, exit status = 10





Exp. No: 4 - Inter-Process Communication: Pipe, Shared Memory, Message Queue
========================================

PROGRAM (Ex4_IPC.c)
----------------------------------------
/* Ex.No:4 Inter-Process Communication: Pipe, Shared Memory, Message Queue */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/msg.h>

#define SIZE 100

struct msgbuf { long type; char text[SIZE]; };

void usePipe() {
    int fd[2];
    char msg[] = "Hello from child using PIPE";
    char buf[SIZE];
    pipe(fd);
    if (fork() == 0) {                       /* child: writer */
        close(fd[0]);
        write(fd[1], msg, strlen(msg) + 1);
        close(fd[1]);
        exit(0);
    }
    close(fd[1]);                            /* parent: reader */
    read(fd[0], buf, SIZE);
    close(fd[0]);
    wait(NULL);
    printf("Sent    : %s\nReceived: %s\n", msg, buf);
    printf(strcmp(msg, buf) == 0 ? "Data is identical\n" : "Data differs\n");
}

void useSharedMemory() {
    char msg[] = "Hello from child using SHARED MEMORY";
    int id = shmget(1234, SIZE, IPC_CREAT | 0666);
    if (id < 0) { perror("shmget"); return; }
    if (fork() == 0) {                       /* child: writer */
        char *p = (char *)shmat(id, NULL, 0);
        strcpy(p, msg);
        shmdt(p);
        exit(0);
    }
    wait(NULL);                              /* parent: reader */
    char *p = (char *)shmat(id, NULL, 0);
    printf("Sent    : %s\nReceived: %s\n", msg, p);
    printf(strcmp(msg, p) == 0 ? "Data is identical\n" : "Data differs\n");
    shmdt(p);
    shmctl(id, IPC_RMID, NULL);              /* remove segment */
}

void useMessageQueue() {
    char msg[] = "Hello from child using MESSAGE QUEUE";
    struct msgbuf m;
    int id = msgget(5678, IPC_CREAT | 0666);
    if (id < 0) { perror("msgget"); return; }
    if (fork() == 0) {                       /* child: sender */
        m.type = 1;
        strcpy(m.text, msg);
        msgsnd(id, &m, strlen(msg) + 1, 0);
        exit(0);
    }
    wait(NULL);                              /* parent: receiver */
    msgrcv(id, &m, SIZE, 1, 0);
    printf("Sent    : %s\nReceived: %s\n", msg, m.text);
    printf(strcmp(msg, m.text) == 0 ? "Data is identical\n" : "Data differs\n");
    msgctl(id, IPC_RMID, NULL);              /* remove queue */
}

int main() {
    int ch;
    do {
        printf("\n1.Pipe  2.Shared Memory  3.Message Queue  4.Exit\nChoice: ");
        scanf("%d", &ch);
        switch (ch) {
            case 1: usePipe(); break;
            case 2: useSharedMemory(); break;
            case 3: useMessageQueue(); break;
            case 4: break;
            default: printf("Invalid choice\n");
        }
    } while (ch != 4);
    return 0;
}

SAMPLE INPUT
----------------------------------------
1
2
3
4

SAMPLE OUTPUT
----------------------------------------

1.Pipe  2.Shared Memory  3.Message Queue  4.Exit
Choice: 1
Sent    : Hello from child using PIPE
Received: Hello from child using PIPE
Data is identical

1.Pipe  2.Shared Memory  3.Message Queue  4.Exit
Choice: 2
Sent    : Hello from child using SHARED MEMORY
Received: Hello from child using SHARED MEMORY
Data is identical

1.Pipe  2.Shared Memory  3.Message Queue  4.Exit
Choice: 3
Sent    : Hello from child using MESSAGE QUEUE
Received: Hello from child using MESSAGE QUEUE
Data is identical

1.Pipe  2.Shared Memory  3.Message Queue  4.Exit
Choice: 4



Exp. No: 5 - Process Synchronization: Producer-Consumer using Mutex and Semaphores
========================================

PROGRAM (Ex5_Producer_Consumer.c)
----------------------------------------
/* Ex.No:5 Producer-Consumer problem using threads, mutex and semaphores
   Compile: gcc Ex5_Producer_Consumer.c -o ex5 -lpthread */
#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>

#define BUFSIZE 5
#define ITEMS   10

int buffer[BUFSIZE];
int in = 0, out = 0;
sem_t empty, full;
pthread_mutex_t mutex;

void *producer(void *arg) {
    for (int i = 1; i <= ITEMS; i++) {
        sem_wait(&empty);                    /* wait for free slot */
        pthread_mutex_lock(&mutex);
        buffer[in] = i;
        printf("Produced item %d at position %d\n", i, in);
        in = (in + 1) % BUFSIZE;
        pthread_mutex_unlock(&mutex);
        sem_post(&full);
        sleep(1);
    }
    return NULL;
}

void *consumer(void *arg) {
    for (int i = 1; i <= ITEMS; i++) {
        sem_wait(&full);                     /* wait for filled slot */
        pthread_mutex_lock(&mutex);
        int item = buffer[out];
        printf("\t\tConsumed item %d from position %d\n", item, out);
        out = (out + 1) % BUFSIZE;
        pthread_mutex_unlock(&mutex);
        sem_post(&empty);
        sleep(2);
    }
    return NULL;
}

int main() {
    pthread_t prod, cons;
    sem_init(&empty, 0, BUFSIZE);
    sem_init(&full, 0, 0);
    pthread_mutex_init(&mutex, NULL);

    pthread_create(&prod, NULL, producer, NULL);
    pthread_create(&cons, NULL, consumer, NULL);
    pthread_join(prod, NULL);
    pthread_join(cons, NULL);

    pthread_mutex_destroy(&mutex);
    sem_destroy(&empty);
    sem_destroy(&full);
    return 0;
}

SAMPLE INPUT
----------------------------------------
(no input needed)

SAMPLE OUTPUT
----------------------------------------
Produced item 1 at position 0
		Consumed item 1 from position 0
Produced item 2 at position 1
		Consumed item 2 from position 1
Produced item 3 at position 2
Produced item 4 at position 3
		Consumed item 3 from position 2
Produced item 5 at position 4
Produced item 6 at position 0
		Consumed item 4 from position 3
Produced item 7 at position 1
Produced item 8 at position 2
		Consumed item 5 from position 4
Produced item 9 at position 3
Produced item 10 at position 4
		Consumed item 6 from position 0
		Consumed item 7 from position 1
		Consumed item 8 from position 2
		Consumed item 9 from position 3
		Consumed item 10 from position 4




Exp. No: 6 - Deadlock Avoidance: Banker's Algorithm
========================================

PROGRAM (Ex6_Bankers_Algorithm.c)
----------------------------------------
/* Ex.No:6 Deadlock Avoidance - Banker's Algorithm */
#include <stdio.h>

int main() {
    int n, m;
    int alloc[10][10], max[10][10], need[10][10], avail[10], work[10];
    int finish[10] = {0}, safe[10], count = 0;

    printf("Enter number of processes: ");
    scanf("%d", &n);
    printf("Enter number of resource types: ");
    scanf("%d", &m);

    printf("Enter Allocation matrix:\n");
    for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) scanf("%d", &alloc[i][j]);
    printf("Enter Max matrix:\n");
    for (int i = 0; i < n; i++) for (int j = 0; j < m; j++) scanf("%d", &max[i][j]);
    printf("Enter Available vector:\n");
    for (int j = 0; j < m; j++) scanf("%d", &avail[j]);

    printf("\nNeed matrix:\n");
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            need[i][j] = max[i][j] - alloc[i][j];
            printf("%d ", need[i][j]);
        }
        printf("\n");
    }

    for (int j = 0; j < m; j++) work[j] = avail[j];

    int found = 1;
    while (found) {
        found = 0;
        for (int i = 0; i < n; i++) {
            if (!finish[i]) {
                int ok = 1;
                for (int j = 0; j < m; j++)
                    if (need[i][j] > work[j]) { ok = 0; break; }
                if (ok) {
                    for (int j = 0; j < m; j++) work[j] += alloc[i][j];
                    finish[i] = 1;
                    safe[count++] = i;
                    found = 1;
                }
            }
        }
    }

    if (count == n) {
        printf("\nSystem is in a SAFE state.\nSafe sequence: ");
        for (int i = 0; i < n; i++) printf("P%d%s", safe[i], i < n - 1 ? " -> " : "\n");
    } else {
        printf("\nSystem is in an UNSAFE state. Deadlock may occur.\n");
    }
    return 0;
}

SAMPLE INPUT
----------------------------------------
5
3
0 1 0
2 0 0
3 0 2
2 1 1
0 0 2
7 5 3
3 2 2
9 0 2
2 2 2
4 3 3
3 3 2

SAMPLE OUTPUT
----------------------------------------
Enter number of processes: 5
Enter number of resource types: 3
Enter Allocation matrix:
0 1 0
2 0 0
3 0 2
2 1 1
0 0 2
Enter Max matrix:
7 5 3
3 2 2
9 0 2
2 2 2
4 3 3
Enter Available vector:
3 3 2

Need matrix:
7 4 3 
1 2 2 
6 0 0 
0 1 1 
4 3 1 

System is in a SAFE state.
Safe sequence: P1 -> P3 -> P4 -> P0 -> P2





Exp. No: 7 - Memory Management: Contiguous Allocation, Paging, Segmentation
========================================

PROGRAM (Ex7_Memory_Management.c)
----------------------------------------
/* Ex.No:7 Memory Management: Contiguous allocation, Paging, Segmentation */
#include <stdio.h>

/* strategy: 1 = First Fit, 2 = Best Fit, 3 = Worst Fit */
void contiguous(int strategy) {
    int nb, np, block[20], orig[20], proc[20], alloc[20];
    printf("Enter number of memory blocks: ");
    scanf("%d", &nb);
    printf("Enter block sizes: ");
    for (int i = 0; i < nb; i++) { scanf("%d", &block[i]); orig[i] = block[i]; }
    printf("Enter number of processes: ");
    scanf("%d", &np);
    printf("Enter process sizes: ");
    for (int i = 0; i < np; i++) scanf("%d", &proc[i]);

    for (int i = 0; i < np; i++) {
        int sel = -1;
        alloc[i] = -1;
        for (int j = 0; j < nb; j++) {
            if (block[j] >= proc[i]) {
                if (strategy == 1) { sel = j; break; }
                if (sel == -1 ||
                    (strategy == 2 && block[j] < block[sel]) ||
                    (strategy == 3 && block[j] > block[sel])) sel = j;
            }
        }
        if (sel != -1) { alloc[i] = sel; block[sel] -= proc[i]; }
    }

    printf("\nProcess\tSize\tBlock\n");
    for (int i = 0; i < np; i++) {
        if (alloc[i] != -1) printf("P%d\t%d\tB%d\n", i + 1, proc[i], alloc[i] + 1);
        else printf("P%d\t%d\tNot allotted\n", i + 1, proc[i]);
    }
    printf("\nBlock\tOriginal\tRemaining\n");
    for (int j = 0; j < nb; j++) printf("B%d\t%d\t\t%d\n", j + 1, orig[j], block[j]);
}

void paging() {
    int pages, ps, frame[20], la;
    printf("Enter page size: ");
    scanf("%d", &ps);
    printf("Enter number of pages: ");
    scanf("%d", &pages);
    for (int i = 0; i < pages; i++) {
        printf("Frame number for page %d: ", i);
        scanf("%d", &frame[i]);
    }
    printf("Enter logical address: ");
    scanf("%d", &la);
    int page = la / ps, offset = la % ps;
    if (page >= pages) { printf("Invalid logical address\n"); return; }
    printf("Page number = %d, Offset = %d\n", page, offset);
    printf("Physical address = (%d x %d) + %d = %d\n", frame[page], ps, offset,
           frame[page] * ps + offset);
}

void segmentation() {
    int ns, base[20], limit[20], seg, off;
    printf("Enter number of segments: ");
    scanf("%d", &ns);
    for (int i = 0; i < ns; i++) {
        printf("Segment %d - base, limit: ", i);
        scanf("%d %d", &base[i], &limit[i]);
    }
    printf("Enter segment number and offset: ");
    scanf("%d %d", &seg, &off);
    if (seg < 0 || seg >= ns) { printf("Invalid segment\n"); return; }
    if (off >= limit[seg]) { printf("Error: offset %d exceeds limit %d\n", off, limit[seg]); return; }
    printf("Physical address = %d + %d = %d\n", base[seg], off, base[seg] + off);
}

int main() {
    int ch;
    do {
        printf("\n1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit\nChoice: ");
        scanf("%d", &ch);
        switch (ch) {
            case 1: case 2: case 3: contiguous(ch); break;
            case 4: paging(); break;
            case 5: segmentation(); break;
            case 6: break;
            default: printf("Invalid choice\n");
        }
    } while (ch != 6);
    return 0;
}

SAMPLE INPUT
----------------------------------------
1
5
100 500 200 300 600
4
212 417 112 426
2
5
100 500 200 300 600
4
212 417 112 426
3
5
100 500 200 300 600
4
212 417 112 426
4
100
4
5 2 7 1
250
5
3
1000 400
3000 200
5000 500
1 100
5
3
1000 400
3000 200
5000 500
1 250
6

SAMPLE OUTPUT
----------------------------------------

1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit
Choice: 1
Enter number of memory blocks: 5
Enter block sizes: 100 500 200 300 600
Enter number of processes: 4
Enter process sizes: 212 417 112 426

Process	Size	Block
P1	212	B2
P2	417	B5
P3	112	B2
P4	426	Not allotted

Block	Original	Remaining
B1	100		100
B2	500		176
B3	200		200
B4	300		300
B5	600		183

1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit
Choice: 2
Enter number of memory blocks: 5
Enter block sizes: 100 500 200 300 600
Enter number of processes: 4
Enter process sizes: 212 417 112 426

Process	Size	Block
P1	212	B4
P2	417	B2
P3	112	B3
P4	426	B5

Block	Original	Remaining
B1	100		100
B2	500		83
B3	200		88
B4	300		88
B5	600		174

1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit
Choice: 3
Enter number of memory blocks: 5
Enter block sizes: 100 500 200 300 600
Enter number of processes: 4
Enter process sizes: 212 417 112 426

Process	Size	Block
P1	212	B5
P2	417	B2
P3	112	B5
P4	426	Not allotted

Block	Original	Remaining
B1	100		100
B2	500		83
B3	200		200
B4	300		300
B5	600		276

1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit
Choice: 4
Enter page size: 100
Enter number of pages: 4
Frame number for page 0: 5 2 7 1
Frame number for page 1: Frame number for page 2: Frame number for page 3: Enter logical address: 250
Page number = 2, Offset = 50
Physical address = (7 x 100) + 50 = 750

1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit
Choice: 5
Enter number of segments: 3
Segment 0 - base, limit: 1000 400
Segment 1 - base, limit: 3000 200
Segment 2 - base, limit: 5000 500
Enter segment number and offset: 1 100
Physical address = 3000 + 100 = 3100

1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit
Choice: 5
Enter number of segments: 3
Segment 0 - base, limit: 1000 400
Segment 1 - base, limit: 3000 200
Segment 2 - base, limit: 5000 500
Enter segment number and offset: 1 250
Error: offset 250 exceeds limit 200

1.First Fit  2.Best Fit  3.Worst Fit  4.Paging  5.Segmentation  6.Exit
Choice: 6





Exp. No: 8 - Page Replacement: FIFO, LRU, Optimal
========================================

PROGRAM (Ex8_Page_Replacement.c)
----------------------------------------
/* Ex.No:8 Page Replacement: FIFO, LRU, Optimal */
#include <stdio.h>

int n, f, ref[50];

void printFrames(int fr[], int hit, int page) {
    printf("%d\t", page);
    for (int i = 0; i < f; i++) {
        if (fr[i] == -1) printf("- ");
        else printf("%d ", fr[i]);
    }
    printf("\t%s\n", hit ? "Hit" : "Page Fault");
}

void result(const char *name, int faults) {
    printf("\n%s: Page faults = %d, Hits = %d, Hit ratio = %.2f\n",
           name, faults, n - faults, (float)(n - faults) / n);
}

int search(int fr[], int page) {
    for (int i = 0; i < f; i++) if (fr[i] == page) return i;
    return -1;
}

void fifo() {
    int fr[10], ptr = 0, faults = 0;
    for (int i = 0; i < f; i++) fr[i] = -1;
    printf("\nFIFO\nPage\tFrames\t\tStatus\n");
    for (int i = 0; i < n; i++) {
        int hit = (search(fr, ref[i]) != -1);
        if (!hit) { fr[ptr] = ref[i]; ptr = (ptr + 1) % f; faults++; }
        printFrames(fr, hit, ref[i]);
    }
    result("FIFO", faults);
}

void lru() {
    int fr[10], last[10], faults = 0;
    for (int i = 0; i < f; i++) { fr[i] = -1; last[i] = -1; }
    printf("\nLRU\nPage\tFrames\t\tStatus\n");
    for (int i = 0; i < n; i++) {
        int pos = search(fr, ref[i]);
        int hit = (pos != -1);
        if (hit) last[pos] = i;
        else {
            int v = 0;                       /* empty frame or least recently used */
            for (int j = 0; j < f; j++) {
                if (fr[j] == -1) { v = j; break; }
                if (last[j] < last[v]) v = j;
            }
            fr[v] = ref[i]; last[v] = i; faults++;
        }
        printFrames(fr, hit, ref[i]);
    }
    result("LRU", faults);
}

void optimal() {
    int fr[10], faults = 0;
    for (int i = 0; i < f; i++) fr[i] = -1;
    printf("\nOPTIMAL\nPage\tFrames\t\tStatus\n");
    for (int i = 0; i < n; i++) {
        int hit = (search(fr, ref[i]) != -1);
        if (!hit) {
            int v = -1, farthest = -1;
            for (int j = 0; j < f; j++) {
                if (fr[j] == -1) { v = j; break; }
                int next = n;                /* n means never used again */
                for (int k = i + 1; k < n; k++)
                    if (ref[k] == fr[j]) { next = k; break; }
                if (next > farthest) { farthest = next; v = j; }
            }
            fr[v] = ref[i]; faults++;
        }
        printFrames(fr, hit, ref[i]);
    }
    result("OPTIMAL", faults);
}

int main() {
    int ch;
    printf("Enter length of reference string: ");
    scanf("%d", &n);
    printf("Enter reference string: ");
    for (int i = 0; i < n; i++) scanf("%d", &ref[i]);
    printf("Enter number of frames: ");
    scanf("%d", &f);
    do {
        printf("\n1.FIFO  2.LRU  3.Optimal  4.Exit\nChoice: ");
        scanf("%d", &ch);
        switch (ch) {
            case 1: fifo(); break;
            case 2: lru(); break;
            case 3: optimal(); break;
            case 4: break;
            default: printf("Invalid choice\n");
        }
    } while (ch != 4);
    return 0;
}

SAMPLE INPUT
----------------------------------------
20
7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1
3
1
2
3
4

SAMPLE OUTPUT
----------------------------------------
Enter length of reference string: 20
Enter reference string: 7 0 1 2 0 3 0 4 2 3 0 3 2 1 2 0 1 7 0 1
Enter number of frames: 3

1.FIFO  2.LRU  3.Optimal  4.Exit
Choice: 1

FIFO
Page	Frames		Status
7	7 - - 	Page Fault
0	7 0 - 	Page Fault
1	7 0 1 	Page Fault
2	2 0 1 	Page Fault
0	2 0 1 	Hit
3	2 3 1 	Page Fault
0	2 3 0 	Page Fault
4	4 3 0 	Page Fault
2	4 2 0 	Page Fault
3	4 2 3 	Page Fault
0	0 2 3 	Page Fault
3	0 2 3 	Hit
2	0 2 3 	Hit
1	0 1 3 	Page Fault
2	0 1 2 	Page Fault
0	0 1 2 	Hit
1	0 1 2 	Hit
7	7 1 2 	Page Fault
0	7 0 2 	Page Fault
1	7 0 1 	Page Fault

FIFO: Page faults = 15, Hits = 5, Hit ratio = 0.25

1.FIFO  2.LRU  3.Optimal  4.Exit
Choice: 2

LRU
Page	Frames		Status
7	7 - - 	Page Fault
0	7 0 - 	Page Fault
1	7 0 1 	Page Fault
2	2 0 1 	Page Fault
0	2 0 1 	Hit
3	2 0 3 	Page Fault
0	2 0 3 	Hit
4	4 0 3 	Page Fault
2	4 0 2 	Page Fault
3	4 3 2 	Page Fault
0	0 3 2 	Page Fault
3	0 3 2 	Hit
2	0 3 2 	Hit
1	1 3 2 	Page Fault
2	1 3 2 	Hit
0	1 0 2 	Page Fault
1	1 0 2 	Hit
7	1 0 7 	Page Fault
0	1 0 7 	Hit
1	1 0 7 	Hit

LRU: Page faults = 12, Hits = 8, Hit ratio = 0.40

1.FIFO  2.LRU  3.Optimal  4.Exit
Choice: 3

OPTIMAL
Page	Frames		Status
7	7 - - 	Page Fault
0	7 0 - 	Page Fault
1	7 0 1 	Page Fault
2	2 0 1 	Page Fault
0	2 0 1 	Hit
3	2 0 3 	Page Fault
0	2 0 3 	Hit
4	2 4 3 	Page Fault
2	2 4 3 	Hit
3	2 4 3 	Hit
0	2 0 3 	Page Fault
3	2 0 3 	Hit
2	2 0 3 	Hit
1	2 0 1 	Page Fault
2	2 0 1 	Hit
0	2 0 1 	Hit
1	2 0 1 	Hit
7	7 0 1 	Page Fault
0	7 0 1 	Hit
1	7 0 1 	Hit

OPTIMAL: Page faults = 9, Hits = 11, Hit ratio = 0.55

1.FIFO  2.LRU  3.Optimal  4.Exit
Choice: 4



Exp. No: 9 - File Management using system calls
========================================

PROGRAM (Ex9_File_Management.c)
----------------------------------------
/* Ex.No:9 File Management using system calls: open, write, read, stat, chmod, unlink */
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>

char name[100];

void create() {
    printf("Enter file name: ");
    scanf("%s", name);
    int fd = open(name, O_CREAT | O_EXCL | O_WRONLY, 0644);
    if (fd < 0) { perror("create"); return; }
    printf("File created (descriptor = %d)\n", fd);
    close(fd);
}

void writeFile() {
    char text[200];
    int fd = open(name, O_WRONLY | O_APPEND);
    if (fd < 0) { perror("open"); return; }
    printf("Enter text: ");
    scanf(" %[^\n]", text);
    int b = write(fd, text, strlen(text));
    printf("Bytes written = %d\n", b);
    close(fd);
}

void readFile() {
    char buf[100];
    int r, fd = open(name, O_RDONLY);
    if (fd < 0) { perror("open"); return; }
    printf("File contents:\n");
    while ((r = read(fd, buf, sizeof(buf))) > 0)   /* until read() returns 0 */
        write(1, buf, r);
    printf("\n");
    close(fd);
}

void attributes() {
    struct stat st;
    if (stat(name, &st) < 0) { perror("stat"); return; }
    printf("Size        : %ld bytes\n", (long)st.st_size);
    printf("Inode number: %ld\n", (long)st.st_ino);
    printf("Permission  : %o  ", st.st_mode & 0777);
    printf("%c%c%c%c%c%c%c%c%c\n",
        (st.st_mode & S_IRUSR) ? 'r' : '-', (st.st_mode & S_IWUSR) ? 'w' : '-',
        (st.st_mode & S_IXUSR) ? 'x' : '-', (st.st_mode & S_IRGRP) ? 'r' : '-',
        (st.st_mode & S_IWGRP) ? 'w' : '-', (st.st_mode & S_IXGRP) ? 'x' : '-',
        (st.st_mode & S_IROTH) ? 'r' : '-', (st.st_mode & S_IWOTH) ? 'w' : '-',
        (st.st_mode & S_IXOTH) ? 'x' : '-');
}

void permission() {
    unsigned int mode;
    printf("Enter new permission (octal, e.g. 600): ");
    scanf("%o", &mode);
    if (chmod(name, mode) < 0) { perror("chmod"); return; }
    printf("Permission changed. New attributes:\n");
    attributes();
}

void delete() {
    if (unlink(name) < 0) { perror("unlink"); return; }
    printf("File deleted\n");
    printf("Trying to display attributes again:\n");
    attributes();                            /* fails, confirming deletion */
}

int main() {
    int ch;
    do {
        printf("\n1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit\nChoice: ");
        scanf("%d", &ch);
        switch (ch) {
            case 1: create(); break;
            case 2: writeFile(); break;
            case 3: readFile(); break;
            case 4: attributes(); break;
            case 5: permission(); break;
            case 6: delete(); break;
            case 7: break;
            default: printf("Invalid choice\n");
        }
    } while (ch != 7);
    return 0;
}

SAMPLE INPUT
----------------------------------------
1
test.txt
2
Hello Operating System Lab
3
4
5
600
6
7

SAMPLE OUTPUT
----------------------------------------

1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit
Choice: 1
Enter file name: test.txt
File created (descriptor = 3)

1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit
Choice: 2
Enter text: Hello Operating System Lab
Bytes written = 26

1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit
Choice: 3
File contents:
Hello Operating System Lab

1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit
Choice: 4
Size        : 26 bytes
Inode number: 2801684
Permission  : 644  rw-r--r--

1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit
Choice: 5
Enter new permission (octal, e.g. 600): 600
Permission changed. New attributes:
Size        : 26 bytes
Inode number: 2801684
Permission  : 600  rw-------

1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit
Choice: 6
File deleted
Trying to display attributes again:
stat: No such file or directory

1.Create 2.Write 3.Read 4.Attributes 5.Permission 6.Delete 7.Exit
Choice: 7




Exp. No: 10 - Disk Scheduling: FCFS, SSTF, SCAN, C-SCAN
========================================

PROGRAM (Ex10_Disk_Scheduling.c)
----------------------------------------
/* Ex.No:10 Disk Scheduling: FCFS, SSTF, SCAN, C-SCAN */
#include <stdio.h>
#include <stdlib.h>

int n, head, cyl, req[50];

void show(const char *name, int seq[], int m) {
    int total = 0;
    printf("\n%s\nSeek sequence: ", name);
    for (int i = 0; i < m; i++) {
        printf("%d ", seq[i]);
        if (i > 0) total += abs(seq[i] - seq[i - 1]);
    }
    printf("\nTotal head movement = %d\n", total);
    printf("Average seek length = %.2f\n", (float)total / n);
}

void sortedCopy(int s[]) {
    for (int i = 0; i < n; i++) s[i] = req[i];
    for (int i = 0; i < n - 1; i++)
        for (int j = 0; j < n - i - 1; j++)
            if (s[j] > s[j + 1]) { int t = s[j]; s[j] = s[j + 1]; s[j + 1] = t; }
}

void fcfs() {
    int seq[60], m = 0;
    seq[m++] = head;
    for (int i = 0; i < n; i++) seq[m++] = req[i];
    show("FCFS", seq, m);
}

void sstf() {
    int seq[60], m = 0, done[50] = {0}, cur = head;
    seq[m++] = head;
    for (int k = 0; k < n; k++) {
        int sel = -1;
        for (int i = 0; i < n; i++)
            if (!done[i] && (sel == -1 || abs(req[i] - cur) < abs(req[sel] - cur))) sel = i;
        done[sel] = 1;
        cur = req[sel];
        seq[m++] = cur;
    }
    show("SSTF", seq, m);
}

void scan() {
    int s[50], seq[60], m = 0;
    sortedCopy(s);
    seq[m++] = head;
    for (int i = 0; i < n; i++) if (s[i] >= head) seq[m++] = s[i];   /* towards higher end */
    seq[m++] = cyl - 1;                                              /* reach last cylinder */
    for (int i = n - 1; i >= 0; i--) if (s[i] < head) seq[m++] = s[i]; /* reverse */
    show("SCAN", seq, m);
}

void cscan() {
    int s[50], seq[60], m = 0;
    sortedCopy(s);
    seq[m++] = head;
    for (int i = 0; i < n; i++) if (s[i] >= head) seq[m++] = s[i];
    seq[m++] = cyl - 1;                                              /* last cylinder */
    seq[m++] = 0;                                                    /* jump to cylinder 0 */
    for (int i = 0; i < n; i++) if (s[i] < head) seq[m++] = s[i];   /* continue upward */
    show("C-SCAN", seq, m);
}

int main() {
    int ch;
    printf("Enter number of requests: ");
    scanf("%d", &n);
    printf("Enter request queue: ");
    for (int i = 0; i < n; i++) scanf("%d", &req[i]);
    printf("Enter initial head position: ");
    scanf("%d", &head);
    printf("Enter total number of cylinders: ");
    scanf("%d", &cyl);
    do {
        printf("\n1.FCFS  2.SSTF  3.SCAN  4.C-SCAN  5.Exit\nChoice: ");
        scanf("%d", &ch);
        switch (ch) {
            case 1: fcfs(); break;
            case 2: sstf(); break;
            case 3: scan(); break;
            case 4: cscan(); break;
            case 5: break;
            default: printf("Invalid choice\n");
        }
    } while (ch != 5);
    return 0;
}

SAMPLE INPUT
----------------------------------------
8
98 183 37 122 14 124 65 67
53
200
1
2
3
4
5

SAMPLE OUTPUT
----------------------------------------
Enter number of requests: 8
Enter request queue: 98 183 37 122 14 124 65 67
Enter initial head position: 53
Enter total number of cylinders: 200

1.FCFS  2.SSTF  3.SCAN  4.C-SCAN  5.Exit
Choice: 1

FCFS
Seek sequence: 53 98 183 37 122 14 124 65 67 
Total head movement = 640
Average seek length = 80.00

1.FCFS  2.SSTF  3.SCAN  4.C-SCAN  5.Exit
Choice: 2

SSTF
Seek sequence: 53 65 67 37 14 98 122 124 183 
Total head movement = 236
Average seek length = 29.50

1.FCFS  2.SSTF  3.SCAN  4.C-SCAN  5.Exit
Choice: 3

SCAN
Seek sequence: 53 65 67 98 122 124 183 199 37 14 
Total head movement = 331
Average seek length = 41.38

1.FCFS  2.SSTF  3.SCAN  4.C-SCAN  5.Exit
Choice: 4

C-SCAN
Seek sequence: 53 65 67 98 122 124 183 199 0 14 37 
Total head movement = 382
Average seek length = 47.75

1.FCFS  2.SSTF  3.SCAN  4.C-SCAN  5.Exit
Choice: 5
