package ca.uwaterloo.util;

import java.io.File;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class WekaTest {

	// Global variables
	// count the training data set
	private static int trainingCount = 0;

	// Number of sample points to be recorded
	public static int NUM_POINT = 190;

	// Number of TRUE data in the training data set
	public static int CORRECT_NUM = 25;

	/**
	 * @throws IOException
	 ******************************************************************************/
	public double processData() throws IOException {
		// Initialize result to be -100.0f
		double result = -100.0;

		try {
			// process data with J48
			result = J48Test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// If J48 meets error, then result = -100.0f will be returned
			FileUtil.writeSdcardFile(FileUtil.PATH, FileUtil.LOG_FILENAME,
					"processData e: J48 meets ERROR!\n");
			e.printStackTrace();
		}

		String test_data = "";

		try {
			// Get data from the test data set
			test_data = FileUtil.readSdcardFile(FileUtil.PATH,
					FileUtil.TEST_FILENAME);

			int length = test_data.length();
			int pos = test_data.indexOf("@DATA\n");
			String testData = test_data.substring(pos + 6, length - 2);

			if (result == -10.0f) {
				// still training, TRUE data
				testData += "1" + "\n";
				FileUtil.writeSdcardFile(FileUtil.PATH,
						FileUtil.TRAINING_FILENAME, testData);
			} else if (result == -20.0f) {
				// still training, FALSE data
				testData += "0" + "\n";
				FileUtil.writeSdcardFile(FileUtil.PATH,
						FileUtil.TRAINING_FILENAME, testData);
			} else if (result == 1.0f) {
				// still training, TRUE data
				testData += "1" + "\n";
				FileUtil.writeSdcardFile(FileUtil.PATH,
						FileUtil.BACKUP_FILENAME, testData);
			} else if (result == 0.0f) {
				// still training, FALSE data
				testData += "0" + "\n";
				FileUtil.writeSdcardFile(FileUtil.PATH,
						FileUtil.BACKUP_FILENAME, testData);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	} // processData

	/********************************************************************************/
	public double J48Test() throws Exception {
		ArffLoader trainingAtf = new ArffLoader();
		ArffLoader testAtf = new ArffLoader();

		// Get data from the training data set
		File trainingFile = new File(FileUtil.PATH + "/"
				+ FileUtil.TRAINING_FILENAME);

		trainingAtf.setFile(trainingFile);

		// Get training instances
		Instances instancesTrain = trainingAtf.getDataSet();

		// Set classifying attr's index to be the last column
		instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);

		// Get number of the training instances in the training data set
		int trainingInstances = instancesTrain.numInstances();
		trainingCount = trainingInstances;

		if (trainingInstances < CORRECT_NUM) {
			// still training, TRUE data
			return -10.0f;
		} else if (trainingInstances > CORRECT_NUM - 1
				&& trainingInstances < 50) {
			// still training, FALSE data
			return -20.0f;
		}

		// // Get data from the test data set
		File testFile = new File(FileUtil.PATH + "/" + FileUtil.TEST_FILENAME);

		testAtf.setFile(testFile);

		// Get test instances
		Instances instancesTest = testAtf.getDataSet();

		// Set classifying attr's index to be the last column
		instancesTest.setClassIndex(instancesTest.numAttributes() - 1);

		// Get number of the test instances in the test data set
		int testInstances = instancesTest.numInstances();

		if (testInstances != 1) {
			return -99.0f;
		}

		// Start training
		Classifier m_classifier = new J48();
		m_classifier.buildClassifier(instancesTrain);

		// Get result of the training
		double predicted = m_classifier
				.classifyInstance(instancesTest.instance(0));
		double result = Double.parseDouble(instancesTest.classAttribute().value((int) predicted));

		return result;
	} // J48Test

	public static int getTrainingCount() {
		return trainingCount;
	}

	// RBFNetwork（径向基函数网络或贝叶斯网络）

	// public void RBFNetwork() throws Exception {
	// // 训练语料文件，官方自带的 demo 里有
	// File inputFile = new File(
	// "/Users/HanyeWei/Documents/workspace/Data/data.arff");
	// ArffLoader atf = new ArffLoader();
	// atf.setFile(inputFile);
	// Instances insTrain = atf.getDataSet(); // 读入训练文件
	// // 测试语料文件：随便 copy 一段训练文件出来，做分类的预测准确性校验
	// inputFile = new File(
	// "/Users/HanyeWei/Documents/workspace/Data/test.arff");
	// atf.setFile(inputFile);
	// Instances instancesTest = atf.getDataSet(); // 读入测试文件
	// instancesTest.setClassIndex(0); //
	// 设置分类属性所在行号（第一行为0号），instancesTest.numAttributes()可以取得属性总数
	// double sum = instancesTest.numInstances(), // 测试语料实例数
	// right = 0.0f;
	// insTrain.setClassIndex(0);// 分类属性：第一个字段
	//
	// // LinearRegression rbn = new LinearRegression();
	// RBFNetwork rbn = new RBFNetwork();
	//
	// // 设置入参
	//
	// rbn.setClusteringSeed(10);// k-means的随机种子数-11 10
	//
	// rbn.setDebug(false);// 控制打印信息（无额外信息输出）
	//
	// rbn.setMaxIts(10);// k-means获取中心值的迭代次数，只适用于离散类别的问题
	//
	// rbn.setMinStdDev(0.1);// k-means最低标准偏差
	//
	// rbn.setNumClusters(3);// k-means类别数
	//
	// rbn.setRidge(1.0E-8);
	//
	// rbn.buildClassifier(insTrain);
	//
	// Evaluation eval = new Evaluation(insTrain);
	//
	// eval.evaluateModel(rbn, insTrain);
	//
	// System.out.println("平均绝对误差：" + eval.meanAbsoluteError());// 越小越好
	//
	// System.out.println("均方根误差：" + eval.rootMeanSquaredError());// 越小越好
	//
	// // System.out.println("相关性系数:"+eval.correlationCoefficient());//越接近1越好
	//
	// System.out.println("根均方误差：" + eval.rootMeanSquaredError());// 越小越好
	//
	// System.out.println("是否准确的参考值：" + eval.meanAbsoluteError());// 越小越好
	//
	// for (int i = 0; i < sum; i++)// 测试分类结果
	// {
	// double predicted = rbn.classifyInstance(instancesTest.instance(i));
	// System.out.println("predicted id：" + predicted + ", result："
	// + instancesTest.classAttribute().value((int) predicted)
	// + ", checked result: "
	// + instancesTest.instance(i).classValue());
	// // System.out.println("测试文件的分类值： " +
	// // instancesTest.instance(i).classValue() + ", 记录："
	// // + instancesTest.instance(i));
	// System.out
	// .println("--------------------------------------------------------------");
	//
	// // 如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
	// if (rbn.classifyInstance(instancesTest.instance(i)) == instancesTest
	// .instance(i).classValue()) {
	// right++;// 正确值加1
	// }
	// }
	// // 请将文件内容的第一列 ? 换成正确答案，才能评判分类预测的结果，本例中只是单纯的预测，下面的输出没有意义。
	// System.out.println("RBFNetwork precision:" + (right / sum));
	// // if(right/sum == 1)System.out.println(flag +
	// // "RBFNetwork（径向基函数网络或贝叶斯网络 classification precision:" + (right /
	// // sum));
	// }
	//
	// // MultilayerPerceptron(神经网络)
	//
	// public void MultiplayerPerceptron() throws Exception {
	// // 训练语料文件，官方自带的 demo 里有
	// File inputFile = new File(
	// "/Users/HanyeWei/Documents/workspace/Data/data.arff");
	// ArffLoader atf = new ArffLoader();
	// atf.setFile(inputFile);
	// Instances insTrain = atf.getDataSet(); // 读入训练文件
	//
	// // 测试语料文件：随便 copy 一段训练文件出来，做分类的预测准确性校验
	// inputFile = new File(
	// "/Users/HanyeWei/Documents/workspace/Data/test.arff");
	// atf.setFile(inputFile);
	// Instances instancesTest = atf.getDataSet(); // 读入测试文件
	// instancesTest.setClassIndex(0); //
	// 设置分类属性所在行号（第一行为0号），instancesTest.numAttributes()可以取得属性总数
	//
	// double sum = instancesTest.numInstances(); // 测试语料实例数
	// double right = 0.0f;
	//
	// insTrain.setClassIndex(0);// 分类属性：第一个字段
	// MultilayerPerceptron mp = new MultilayerPerceptron();
	//
	// // 设置入参
	//
	// mp.setGUI(false);// 是否进行图形交互
	//
	// mp.setAutoBuild(true);// 设置网络中的连接和隐层
	//
	// mp.setDebug(false);// 控制打印信息
	//
	// mp.setDecay(false);// 如果为true会降低学习速率
	//
	// mp.setHiddenLayers("a");// 对预测结果几乎没用影响
	//
	// mp.setLearningRate(0.3);// Weights被更新的数量,对预测结果影响很大
	//
	// mp.setMomentum(0.8);// 当更新weights时设置的动量
	//
	// mp.setNormalizeAttributes(true);// 可以优化网络性能
	//
	// mp.setNormalizeNumericClass(true);// 如果预测的是数值型可以提高网络的性能
	//
	// mp.setReset(false);// 必须要在AutoBuild为true的条件下进行设置否则默认即可
	//
	// mp.setSeed(11);// 随机种子数，对预测结果影响大
	//
	// mp.setTrainingTime(300);// 迭代的次数,有一定影响，但是不大
	//
	// mp.setValidationSetSize(20);// 验证百分比，影响大
	//
	// mp.setValidationThreshold(50);// 几乎没用影响
	//
	// mp.setNominalToBinaryFilter(true);// 可以提高性能
	//
	// mp.buildClassifier(insTrain);
	//
	// /*
	// * Evaluation eval=new Evaluation(insTrain);
	// *
	// * eval.evaluateModel(mp, insTrain);
	// *
	// * System.out.println("平均绝对误差："+eval.meanAbsoluteError());//越小越好
	// *
	// * System.out.println("均方根误差："+eval.rootMeanSquaredError());//越小越好
	// *
	// * System.out.println("相关性系数:"+eval.correlationCoefficient());//越接近1越好
	// *
	// * System.out.println("根均方误差："+eval.rootMeanSquaredError());//越小越好
	// *
	// * System.err.println("是否准确的参考值："+eval.meanAbsoluteError());//越小越好
	// */
	//
	// // insTrain.setClassIndex(0);
	// Evaluation eval = new Evaluation(insTrain);
	//
	// eval.evaluateModel(mp, insTrain);
	//
	// for (int i = 0; i < sum; i++)// 测试分类结果
	// {
	// double predicted = mp.classifyInstance(instancesTest.instance(i));
	// System.out.println("predicted id：" + predicted + ", result："
	// + instancesTest.classAttribute().value((int) predicted)
	// + ", checked result:"
	// + instancesTest.instance(i).classValue());
	// // System.out.println("checked result： " +
	// // instancesTest.instance(i).classValue() + ", record："
	// // + instancesTest.instance(i));
	// System.out
	// .println("--------------------------------------------------------------");
	//
	// // 如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
	// if (mp.classifyInstance(instancesTest.instance(i)) == instancesTest
	// .instance(i).classValue()) {
	// right++;// 正确值加1
	// }
	// }
	// // 请将文件内容的第一列 ? 换成正确答案，才能评判分类预测的结果，本例中只是单纯的预测，下面的输出没有意义。
	// System.out.println("MultiplayerPerceptron precision:" + (right / sum));
	// }
}