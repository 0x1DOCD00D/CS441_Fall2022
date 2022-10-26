# Homework 3
### Description: create cloud simulators for evaluating executions of applications in cloud datacenters with different characteristics and deployment models.
### Grade: 15%

## Preliminaries
As part of the previous homework assignment you learned to create and manage your Git repository, create your application in Scala, create tests using widely popular Scalatest framework, and expand on the provided SBT build and run script for your application. Your job is to create a large-scale simulation of a cloud organization with multiple datacenters that use sophisticated load balancers, autoscalers, sophisticated schedulers and many other important elements of computing clouds that we learned in this course.

First things first, if you haven't done so, you must create your account at either [BitBucket](https://bitbucket.org/) or [Github](https://github.com/), which are Git repo management systems. Please make sure that you write your name in your README.md in your repo as it is specified on the class roster. Since it is a large class, please use your UIC email address for communications and avoid emails from other accounts like funnybunny2000@gmail.com. If you don't receive a response within 12 hours, please contact your TA or me, it may be a case that your direct emails went to the spam folder.

Next, if you haven't done so, you will install [IntelliJ](https://www.jetbrains.com/student/) with your academic license, the JDK, the Scala runtime and the IntelliJ Scala plugin and the [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html) and make sure that you can create, compile, and run Java and Scala programs. Please make sure that you can run [various Java tools from your chosen JDK between versions 8 and 18](https://docs.oracle.com/en/java/javase/index.html).

As in previous homeworks you will use logging and configuration management frameworks. You will comment your code extensively and supply logging statements at different logging levels (e.g., TRACE, INFO, WARN, ERROR) to record information at some salient points in the executions of your programs. All input and configuration variables must be supplied through configuration files -- hardcoding these values in the source code is prohibited and will be punished by taking a large percentage of points from your total grade! You are expected to use [Logback](https://logback.qos.ch/) and [SLFL4J](https://www.slf4j.org/) for logging and [Typesafe Conguration Library](https://github.com/lightbend/config) for managing configuration files. These and other libraries should be imported into your project using your script [build.sbt](https://www.scala-sbt.org). These libraries and frameworks are widely used in the industry, so learning them is the time well spent to improve your resumes.

Even though CloudSimPlus is written in Java, you can create your simulations using Scala. No matter whether you use Java or Scala you should create a fully pure functional (not imperative) implementation. When creating your simulation program in Scala, you should avoid using **var**s and while/for loops that iterate over collections using [induction variables](https://en.wikipedia.org/wiki/Induction_variable). Instead, you should learn to use collection methods **map**, **flatMap**, **foreach**, **filter** and many others with lambda functions, which make your code linear and easy to understand. Also, avoid mutable variables that expose the internal states of your modules at all cost. Points will be deducted for having unreasonable **var**s and inductive variable loops without explanation why mutation is needed in your code unless it is confined to method scopes - you can always do without it.

## Overview
In this homework, you are going to create cloud computing organization with multiple datacenters and run jobs on them. Of course, creating real cloud computing datacenters takes hundreds of millions of dollars and acres of land and a lot of complicated equipment, and you don't want to spend your money and resources creating physical cloud datacenters for this homework ;-). Instead, we have a cloud simulator, a software package that models the cloud environments and operates different cloud models that we study in the lectures. We will use *Cloud2SimPlus*, a simulation framework that is available from [Github](https://github.com/cloudsimplus/cloudsimplus) with a set of libraries and many examples for modeling and simulating cloud computing infrastructure and services.

[CloudSimPlus website](https://cloudsimplus.org/) contains a wealth of information and it is your starting point. It is recommended that you learn more about *CloudSim* -- you will find an [old online course on CloudSim](https://www.superwits.com/library/cloudsim-simulation-framework) and [many youtube videos like this one on using CloudSimPlus](https://www.youtube.com/watch?v=Gxc8-mlvWBI) and your starting point is to create a baseline Scala project and to run [examples that are provided in the Github repo](https://github.com/cloudsimplus/cloudsimplus-examples). You can add the [CloudSimPlus dependency](https://mvnrepository.com/artifact/org.cloudsimplus/cloudsim-plus) to the project's build.sbt. Those who want to read more about modeling physical systems and creating simulations can find ample resources on the Internet - I recommend the following paper by [Any Maria on Introduction to Modeling and Simulation](http://acqnotes.com/Attachments/White%20Paper%20Introduction%20to%20Modeling%20and%20Simulation%20by%20Anu%20Maria.pdf).

This homework script is written using a retroscripting technique, in which the homework outlines are generally and loosely drawn, and the individual students improvise to create the implementation that fits their refined objectives. In doing so, students are expected to stay within the basic requirements of the homework and they are free to experiments. Asking questions is important, so please ask away at Piazza!

## Functionality
Once you installed and configured CloudSimPlus, your job is to run examples supplied with the frameworks to perform two or more simulations where you will evaluate two or more datacenters with different characteristics (e.g., operating systems, costs, devices) and policies. Imagine that you are a cloud computing broker and you purchase computing time in bulk from different cloud providers and you sell this time to your customers, so that they can execute their jobs, i.e., cloudlets on the infrastructure of these cloud providers that have different policies and constraints. As a broker, your job is to buy the computing time cheaply and sell it at a good markup. One way to achieve it is to take cloudlets from your customers and estimate how long they will execute. Then you charge for executing cloudlets some fixed fee that represent your cost of resources summarily. Some cloudlets may execute longer than you expected, the other execute faster. If your revenue exceeds your expenses for buying the cloud computing time in bulk, you are in business, otherwise, you will go bankrupt!

There are different policies that datacenters can use for allocating Virtual Machines (VMs) to hosts, scheduling them for executions on those hosts, determining how network bandwidth is provisioned, and for scheduling cloudlets to execute on different VMs. Randomly assigning these cloudlets to different datacenters may result in situation where the executions of these cloudlets are inefficient and they takes a long time. As a result, you exhaust your supply of the purchased cloud time and you may have to refund the money to your customers, since you cannot fulfil the agreement, and you will go bankrupt. Modeling and simulating the executions of cloudlets in your clouds may help you chose a proper model for your business.

Once you installed and configured CloudSim and ran its examples, your next job will be to create simulations where you will evaluate a large cloud provider with many datacenters with different characteristics (e.g., operating systems, costs, devices) and policies. You will form a stream of jobs, dynamically, and feed them into your simulation. You will design your own datacenter with your own network switches and network links. You can organize cloudlets into tasks to accomplish the same job (e.g., a map reduce job where some cloudlets represent mappers and the other cloudlets represent reducers). There are different policies that datacenters can use for allocating Virtual Machines (VMs) to hosts, scheduling them for executions on those hosts, determining how network bandwidth is provisioned, and for scheduling cloudlets to execute on different VMs. Randomly assigning these cloudlets to different datacenters may result in situation where the execution is inefficient and takes a long time. Using a more clever algorithm like assigning tasks to specific clusters where the data is located may lead to more efficient cloud provider services.

Consider a snippet of the code below from one of the examples that come from the documentation on CloudSim. In it, a network cloud datacenter is created with network hardware that is used to organize hosts in a connected network. VMs can exchange packets/messages using a chosen network topology. Depending on your simulation construct, you may view different levels of performances.
```java
protected final NetworkDatacenter createDatacenter() {
  final int numberOfHosts = EdgeSwitch.PORTS * AggregateSwitch.PORTS * RootSwitch.PORTS;
  List<Host> hostList = new ArrayList<>(numberOfHosts);
  for (int i = 0; i < numberOfHosts; i++) {
      List<Pe> peList = createPEs(HOST_PES, HOST_MIPS);
      Host host = new NetworkHost(HOST_RAM, HOST_BW, HOST_STORAGE, peList)
                    .setRamProvisioner(new ResourceProvisionerSimple())
                    .setBwProvisioner(new ResourceProvisionerSimple())
                    .setVmScheduler(new VmSchedulerTimeShared());
      hostList.add(host);
  }

  NetworkDatacenter dc =
          new NetworkDatacenter(
                  simulation, hostList, new VmAllocationPolicySimple());
  dc.setSchedulingInterval(SCHEDULING_INTERVAL);
  dc.getCharacteristics()
        .setCostPerSecond(COST)
        .setCostPerMem(COST_PER_MEM)
        .setCostPerStorage(COST_PER_STORAGE)
        .setCostPerBw(COST_PER_BW);
  createNetwork(dc);
  return dc;
}
```

Your homework can be divided roughly into five steps. First, you learn how CloudSim is organized and what your building blocks from the CloudSim framework you will use. You should import the source code of CloudSim Plus into IntelliJ and explore its classes, interfaces, and dependencies. Second, you design your own cloud provider organization down to rack/cluster organization as we will study in our lectures. You will add various policies and load balancing heuristics like randomly allocating tasks to machines or using data locality to guide the task allocation. Next, you will create an implementation of the simulation(s) of your cloud provider using CloudSim. Fourth, you will run multiple simulations with different parameters, statistically analyze the results and report them in your documentation with explanations why some cloud architectures are more efficient than the others in your simulations.

The final fifth step is the following. You will implement three datacenters each of which offers different mixes of SaaS, PaaS, IaaS and FaaS model implementations with various pricing criteria. A broker will decide to which datacenter your tasks will be sent based on additional information provided with those tasks, e.g., accessing SaaS services of some application or deploying your own software stack that will service some tasks. You will describe your design of the implementation of your simulation and how your cloud organizations/pricing models lead to different results and explain these results. In your implementation you should use the following tools to build a realistic cloud organization.

- Implement policies for a datacenter to select a host to place or migrate a VM based on some criteria defined by your cloud datacenter design, e.g., to find the first host having suitable resources to place a given VM using the algorithm Oktopus that we study in this course.
- Provide complex cloudlet executions that diffuse subtasks across the network topology using some heuristic/algorithm.
- Design the network organization of cloud datacenters using selected network topologies and place routers/switches in/between datacenters.
- Implement star, bus, ring, tree and hybrid network topologies for different datacenters and incorporate the knowledge of different topologies into brokers' algorithms of assigning tasks to datacenters, e.g., a complex map/reduce cloudlet may be assigned to a datacenter with a tree network topology.
- Design and implement your heuristics/algorithm for autoscaling, i.e., to define the condition/[Predicate](https://javadoc.io/doc/org.cloudsimplus/cloudsim-plus/latest/org/cloudsimplus/autoscaling/package-summary.html) to fire the scaling mechanism.
- Implement at least two scheduling algorithms to schedule the execution of multiple VMs inside a given host using the material that we learn in this class.
- Design and implement model utilization of resources such as CPU, RAM and network bandwidth by defining how resources are used by a Cloudlet along the simulation time.
- Model and implement power consumption in your cloud organization.

## Baseline
Your absolute minimum gradeable baseline project can be based on the examples that come from the CloudSimPlus repo. To be considered for grading, your project should include at least one of your simulation programs written in Scala, your project should be buildable using the SBT, and your documentation must specify how you create and evaluate your simulated clouds based on the cloud models that we learn in the class/textbooks. Your documentation must include the results of your simulation, the measurement of the runtime parameters of the simulator (e.g., CPU and RAM utilization) and your explanations of how these results help you with your simulation objectives (e.g., choose the right cloud model and configuration). Simply copying Java programs from examples and modifying them a bit (e.g., rename some variables) will result in desk-rejecting your submission.

## Baseline Submission
Your baseline project submission should include your implementation, a conceptual explanation in the document or in the comments in the source code of how your mapper and reducer work to solve the problem, and the documentation that describe the build and runtime process, to be considered for grading. Your project submission should include all your source code as well as non-code artifacts (e.g., configuration files), your project should be buildable using the SBT, and your documentation must specify how you paritioned the data and what input/outputs are.

## Collaboration
You can post questions and replies, statements, comments, discussion, etc. on Teams using the corresponding channel. For this homework, feel free to share your ideas, mistakes, code fragments, commands from scripts, and some of your technical solutions with the rest of the class, and you can ask and advise others using Teams on where resources and sample programs can be found on the Internet, how to resolve dependencies and configuration issues. When posting question and answers on Teams, please make sure that you selected the appropriate channel, to ensure that all discussion threads can be easily located. Active participants and problem solvers will receive bonuses from the big brother :-) who is watching your exchanges (i.e., your class instructor and your TA). However, *you must not describe your specific details related to your intellectual creation w.r.t. constructing your simulation models!*

## Git logistics
**This is an individual homework.** If you read this description it means that you located the [Github repo for this homework](https://github.com/0x1DOCD00D/CS441_Fall2022). Please remember to grant a read access to your repository to your TA and your instructor. You can commit and push your code as many times as you want. Your code will not be visible and it should not be visible to other students - your repository should be private. Announcing a link to your public repo for this homework or inviting other students to join your fork for an individual homework before the submission deadline will result in losing your grade. For grading, only the latest commit timed before the deadline will be considered. **If your first commit will be pushed after the deadline, your grade for the homework will be zero**. For those of you who struggle with the Git, I recommend a book by Ryan Hodson on Ry's Git Tutorial. The other book called Pro Git is written by Scott Chacon and Ben Straub and published by Apress and it is [freely available](https://git-scm.com/book/en/v2/). There are multiple videos on youtube that go into details of the Git organization and use.

Please follow this naming convention to designate your authorship while submitting your work in README.md: "Firstname Lastname" without quotes, where you specify your first and last names **exactly as you are registered with the University system**, so that we can easily recognize your submission. I repeat, make sure that you will give both your TA and the course instructor the read/write access to your *private forked repository* so that we can leave the file feedback.txt with the explanation of the grade assigned to your homework.

## Discussions and submission
As it is mentioned above, you can post questions and replies, statements, comments, discussion, etc. on Teams. Remember that you cannot share your code and your solutions privately, but you can ask and advise others using Teams and StackOverflow or some other developer networks where resources and sample programs can be found on the Internet, how to resolve dependencies and configuration issues. Yet, your implementation should be your own and you cannot share it. Alternatively, you cannot copy and paste someone else's implementation and put your name on it. Your submissions will be checked for plagiarism. **Copying code from your classmates or from some sites on the Internet will result in severe academic penalties up to the termination of your enrollment in the University**.


## Submission deadline and logistics
Sunday, November 20, 2022 at 10PM CST by submitting the link to your homework repo in the Teams Assignments channel. Your submission repo will include the code for the program, your documentation with instructions and detailed explanations on how to assemble and deploy your program along with the results of your program execution, the link to the video and a document that explains these results based on the characteristics and the configuration parameters of your log generator, and what the limitations of your implementation are. Again, do not forget, please make sure that you will give both your TAs and your instructor the read access to your private repository. Your code should compile and run from the command line using the commands **sbt clean compile test** and **sbt clean compile run**. Also, you project should be IntelliJ friendly, i.e., your graders should be able to import your code into IntelliJ and run from there. Use .gitignore to exlude files that should not be pushed into the repo.

## Evaluation criteria
- the maximum grade for this homework is 15%. Points are subtracted from this maximum grade: for example, saying that 2% is lost if some requirement is not completed means that the resulting grade will be 15%-2% => 13%; if the core homework functionality does not work or it is not implemented as specified in your documentation, your grade will be zero;
- only some basic simulations examples from some repos are given and nothing else is done: zero grade;
- having less than five unit and/or integration scalatests: up to 10% lost;
- missing comments and explanations from your simulation program: up to 10% lost;
- logging is not used in your programs: up to 5% lost;
- hardcoding the input values in the source code instead of using the suggested configuration libraries: up to 5% lost;
- for each used *var* for heap-based shared variables or mutable collections: 0.3% lost;
- for each used *while* or *for* or other loops with induction variables to iterate over a collection: 0.5% lost;
- no instructions in README.md on how to install and run your program: up to 10% lost;
- the program crashes without completing the core functionality: up to 15% lost;
- the documentation exists but it is insufficient to understand your program design and models and how you assembled and deployed all components of your simulation: up to 15% lost;
- the minimum grade for this homework cannot be less than zero.

That's it, folks!
