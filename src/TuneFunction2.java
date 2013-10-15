import java.util.Properties;

import javabbob.JNIfgeneric;

import org.vu.contest.team24.tuner.TunerContestEvaluation;

public class TuneFunction2 implements TunerContestEvaluation {
	private double target_;
	private JNIfgeneric function_ = null;
	private double best_;
	private int evaluations_;

	private String multimodal_ = "true";
	private String regular_ = "true";
	private String separable_ = "false";
	private String evals_ = Integer.toString(100000);

	public TuneFunction2() {
		this.best_ = 0.0D;

		this.evaluations_ = 0;

		this.function_ = new JNIfgeneric();
		JNIfgeneric.Params params = new JNIfgeneric.Params();
		params.algName = "";
		params.comments = "";
		JNIfgeneric.makeBBOBdirs("tmp", true);
		this.function_.initBBOB(16, 1, 10, "tmp", params);
		this.target_ = this.function_.getFtarget();
	}

	public Object evaluate(Object result) {
		if (!(result instanceof double[]))
			throw new IllegalArgumentException();
		double[] ind = (double[]) result;
		if (ind.length != 10)
			throw new IllegalArgumentException();

		if (this.evaluations_ > 100000)
			return null;

		double fp = (this.function_.evaluate(ind) - this.target_)
				/ (77.0D - this.target_);
		double f = 10.0D * Math.exp(-5.0D * fp);
		if (f > 10.0D)
			f = 10.0D;
		if (f > this.best_)
			this.best_ = f;
		this.evaluations_ += 1;

		return new Double(f);
	}

	public Object getData(Object arg0) {
		return null;
	}

	public double getFinalResult() {
		return this.best_;
	}

	public Properties getProperties() {
		Properties props = new Properties();
		props.put("Multimodal", this.multimodal_);
		props.put("Regular", this.regular_);
		props.put("Separable", this.separable_);
		props.put("Evaluations", this.evals_);
		return props;
	}

	@Override
	public void reset() {
		this.best_ = 0.0D;
		this.evaluations_ = 0;
	}
}
